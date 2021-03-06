package ca.loobo.restbot;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.util.UriComponentsBuilder;

import ca.loobo.restbot.exceptions.CaseNotExecutedException;
import ca.loobo.restbot.exceptions.ResourceNotFoundException;
import ca.loobo.restbot.exceptions.ResourceNotReadableException;
import ca.loobo.restbot.param.RangeValueParamProducer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;

public abstract class AbstractCase implements CaseElements {
	final static Logger logger = LoggerFactory.getLogger(AbstractCase.class);
	
	private String id;										//case ID from resource file
	private String url;										//generated by this test with parameters from resource file
	private List<String> errors;							//generated by this test for explaining the test result
	private List<String> infos;
	private String rawResponse;								// response from catalog-api
	private Object response=new Object();					// Object from parsing rawResponse
	
	@JsonIgnore
	private boolean executed = false;

	@JsonIgnore
	private String responseTemplate;
	
	@JsonIgnore
	private String responseSchema;

	@JsonIgnore
	private Context context;

	@JsonIgnore
	List<Case> childCases;
	
	public AbstractCase(Context ctx) {
		this.context = ctx;
		this.errors = new LinkedList<String>();
		this.infos = new LinkedList<String>();
	}

	public List<String> getErrors() {
		return this.errors;
	}
	
	/*
	 * Some information need to display in the result
	 */
	public List<String> getInfos() {
		return this.infos;
	}
	
	public void setMeta(String metaName, String value) {
		try {
			String fieldName = metaName.toLowerCase();
			this.getMetas().put(fieldName, value);
			Field field = this.getClass().getField(fieldName);
			field.set(this, value);
		} catch (Exception e) {
			//ignore
		} 
	}
	
	public String getDescription() {
		String s = getMetas().get(META_DESCRIPTION);
		return TextUtils.isEmpty(s) ? "" : s;
	}
	
	@JsonIgnore
	public String getRawResponse() {
		return this.rawResponse;
	}
	
	public void setRawResponse(String rawResponse) {
		this.rawResponse = rawResponse;
	}
	
	@JsonIgnore
	public Object getResponse() {
		if (TextUtils.isEmpty(this.rawResponse)) {
			return null;
		}
		try {				
			//this.response = new Gson().fromJson(this.rawResponse, Object.class);	
			this.response = new ObjectMapper().readValue(this.rawResponse, Object.class);
		}
		catch(Exception e) {
			e.printStackTrace();
			errors.add(e.getMessage());
		}
		
		return this.response;
	}
	
	public void setResponse(Object r) {
		this.response = r;
	}
	

	final public boolean isPassed() {
		return errors.size()==0;
	}
	
	//generate the url in the format of ?param1={param1}&param2={param2}
	//the pattern is used by Horatiu's test framework
	@JsonIgnore
	final public String getUrlPattern() {
		StringBuilder sb = new StringBuilder();
		for(Entry<String, String> pair : getQueryParams().entrySet()) {
			if (sb.length()>0) {
				sb.append("&");
			}
			sb.append(pair.getKey());
			sb.append("=");
			sb.append(pair.getValue());
		}
		
		return sb.toString();
	}
	
	final public void addError(String errMsg) {
		this.errors.add(errMsg);
	}
	
	final public void addInfo(String msg) {
		this.infos.add(msg);
	}
	
	final public void addExpection(String name, String value) {
		this.getExpectations().put(name, ca.loobo.restbot.utils.StringUtils.extend(value, this.context.getParamMap()));
	}
	
	final public String getUrl() {
		if (this.url == null) {
			String p;
			UriComponentsBuilder builder = context.getUriBuilder();
			if (StringUtils.isNotBlank(p=getMetas().get(META_PATH))) {				
				builder.path(p.trim());
			}
			
			if (StringUtils.isNotBlank(p=getMetas().get(META_COMMON_PARAMS))) {				
				builder.query(ca.loobo.restbot.utils.StringUtils.extend(p, context.getParamMap()));
			}
			
			for(Entry<String, String> pair : getQueryParams().entrySet()) {
				String name = pair.getKey();
				String value = pair.getValue();
				
				String parsedValue = context.parseParamValue(context, value);
				getQueryParams().put(name, parsedValue);
				builder.queryParam(name, parsedValue);
			}
	
			this.url = builder.build(false).toUriString();
			this.url = ca.loobo.restbot.utils.StringUtils.extend(this.url, this.context.getParamMap());
		}
		return this.url;
	}


	@JsonIgnore
	final public String getResponseTemplate() {
		if (responseTemplate != null) {
			return this.responseTemplate;
		}
		
		String s;
		Resource templateResource;
		String templatePath = this.getMetas().get(META_RESPONSE_TEMPLATE);
		if (StringUtils.isNotBlank(s=this.getMetas().get(META_RESOURCE_FOLDER))) {
			templatePath = s + "/" + templatePath;
		}
		
		//Check if default response template file exists in resource folder if it is not defined in test case
		//Default response template file has name in the format of template_<CASE ID>.json
		if (templatePath == null || "".equals(templatePath.trim())) {
			try {
				templateResource = context.getResource("template_" + this.getId() + ".json");
				this.getMetas().put(META_RESPONSE_TEMPLATE, templateResource.getFilename());
				logger.debug("found response template {}", this.getMetas().get(META_RESPONSE_TEMPLATE));
			} catch(ResourceNotFoundException e) {
				return null;
			}
		}
		else {
			templateResource = context.getResource(templatePath);
			if (templateResource == null || !templateResource.isReadable()) {
				throw new ResourceNotReadableException(templatePath);
			}
		}
		

		try {
			this.responseTemplate = IOUtils.toString(templateResource.getInputStream(), "UTF-8");
			return this.responseTemplate;
		} catch (IOException e) {
			throw new ResourceNotReadableException(templateResource.getFilename());
		}
	}
	
	/**
	 * Schema file must be in the standard format schema_name.json
	 * @return
	 */
	@JsonIgnore
	final public String getResponseSchema() {
		if (responseSchema != null) {
			return this.responseSchema;
		}
		
		String schemaPath = this.getMetas().get(META_RESPONSE_SCHEMA);
		if (schemaPath == null || "".equals(schemaPath.trim())) {
			return null;
		}
		if (StringUtils.isEmpty(Files.getFileExtension(schemaPath))) {
			schemaPath += ".json";
		}
		
		Resource templateResource = context.getResource(schemaPath);
		if (templateResource == null) {
			throw new ResourceNotFoundException(schemaPath);
		}
		try {
			this.responseSchema = IOUtils.toString(templateResource.getInputStream(), "UTF-8");
			return this.responseSchema;
		} catch (IOException e) {
			throw new ResourceNotReadableException(templateResource.getFilename());
		}
	}

	@JsonIgnore
	final public void assertExecuted() {
		if (!this.executed) {
			throw new CaseNotExecutedException(this.id);
		}
	}

	final public String getId() {
		this.id = this.getMetas().get(META_ID);
		return this.id;
	}

	@JsonIgnore
	final public Context getContext() {
		return this.context;
	}

	
	@JsonIgnore
	public boolean isExecuted() {
		return executed;
	}

	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	@JsonIgnore
	final public List<String> getDependencies() {
		List<String> dependencies = new LinkedList<String>();
		String commaSeperatedValue = this.getMetas().get(META_DEPENDENCIES);
		if (commaSeperatedValue == null) {
			return dependencies;
		}
		
		String[] caseIds = commaSeperatedValue.split(",");
		for(String caseId : caseIds) {
			String c = caseId.trim();
			if (!TextUtils.isEmpty(c)) {
				dependencies.add(caseId.trim());
			}
		}
		
		return dependencies;
	}
	
	final public boolean isGroupCase() {
		for(String pv : this.getQueryParams().values()) {
			if (RangeValueParamProducer.matches(pv)) {
				return true;
			}
		}
		
		return false;
	}

}
