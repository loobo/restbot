package ca.loobo.restbot;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import ca.loobo.restbot.exceptions.CaseNotFoundException;
import ca.loobo.restbot.exceptions.ResourceNotFoundException;
import ca.loobo.restbot.param.ParamProducerFactory;

public class Context implements CaseFinder {
	static final Logger logger = LoggerFactory.getLogger(Context.class);

	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String SERVICE_PATH = "servicePath";
	public static final String FIXED_PARAMS = "fixedParams";
	public static final String RESOURCE_FOLDER = "resourceFolder";
	public static final String DB_URL="dbUrl";
	public static final String DB_USERNAME="dbUsername";
	public static final String DB_PASSWORD="dbPassword";

	//use LinkedHashMap to keep the elements order the same as they are inserted
	private LinkedHashMap <String, Case> cases = new LinkedHashMap <String, Case>();
	private String host;
	private Integer port;
	private Result result;
	private String outDir = "output";
	private String resDir = "resources";
	private Map<String, String> params = new HashMap<String, String>();
	private ParamProducerFactory paramProducerFactory;
	private List<Pattern> allowFilters;
	
	public Context() {
		result = new Result(this);
		allowFilters = new LinkedList<Pattern>();
	}
	
	public void initialize() {
		Assert.notNull(getHost(), "host is not defined");
		paramProducerFactory = new ParamProducerFactory(this);		
	}
	
	public String getHost() {
		return host == null ? getParam(HOST) : host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port == null ? getParam(PORT) : port.toString();
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Collection<Case> getCases() {
		return this.cases.values();
	}
	
	private boolean isAllowed(String caseId) {
		if (allowFilters.size()==0) {
			return true;
		}
		
		for(Pattern m : allowFilters) {
			if (m.matcher(caseId).matches()) {
				return true;
			}
		}
		return false;
	}
	
	public void addAllowFilter(String filter) {
		this.allowFilters.add(Pattern.compile(filter));
	}
	
	public void addCase(String caseId, Case apicase) {
		if (isAllowed(caseId)) {
			this.cases.put(caseId, apicase);
		}
	}
	
	public Case getCase(String caseId) {
		Case c = this.cases.get(caseId);
		if (c == null) {
			throw new CaseNotFoundException(caseId);
		}
		
		return c;
	}
	
	public void addFinishedCase(Case c) {
		this.result.add(c);
	}

	public Result getResult() {
		return this.result;
	}
	

	Resource getResource(String resourcePath) {
		Resource resource;

		String path = getParam(RESOURCE_FOLDER);
		if (StringUtils.isBlank(path)) {
			path = System.getProperty("user.dir");
		}
		
		path += "/" + resourcePath;
		resource = Configurator.instance().getApplicationContext().getResource(path);
		if (resource != null && resource.isReadable()) {
			return resource;
		}		

		// find resource in Windows-style absolute path
		else if (path.matches("[a-zA-Z]+:.*")) {
			logger.trace("not found {}", path);
			throw new ResourceNotFoundException("Couldn't read resource file " + path);
		}
		// find resource in path relative to the current folder
		else {
			path = "file:" + path;
		}
		
		logger.debug("reading {}", path);
		resource = Configurator.instance().getApplicationContext().getResource(path);
		
		if (resource == null || !resource.isReadable()) {
			throw new ResourceNotFoundException("Couldn't read resource file " + path);
		}

		return resource;
	}
	
	public String getOutputDir() {
		File dir = new File(outDir);
		if (!dir.isDirectory()) {
			Assert.isTrue(dir.mkdir(), "make output folder");
		}
		
		return outDir;
	}
	/**
	 * Set output directory, all output files will be saved in this folder
	 * 
	 * @param outDir
	 * @return
	 */
	public void setOutputDir(String outDir) {
		this.outDir = outDir;
		getOutputDir();
	}
	
	public String getResourceDir() {
		return resDir;
	}
	public void setResourceDir(String resDir) {
		this.resDir = resDir;
	}

	public Map<String, String> getParamMap() {
		return this.params;
	}
	
	/*
	 * System property always overwrite parameter defined in Excel test schema
	 * Applied parameters includes host, port, resourceFolder
	 */
	public String getParam(String paramName) {
		return System.getProperty(paramName, params.get(paramName));
	}


	public UriComponentsBuilder getUriBuilder() {
		String p;
		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
		builder.scheme("http");
		builder.host(getHost());
		if (StringUtils.isNotBlank(p=getPort())) {
			builder.port(Integer.valueOf(p.trim()));
		}
		
		if (StringUtils.isNotBlank(p=params.get(SERVICE_PATH))) {
			builder.path(p.trim());
		}
		
		if (StringUtils.isNotBlank(p=params.get(FIXED_PARAMS))) {
			builder.query(p.trim());
		}
		

		return builder;
	}

	public void addVariable(String name, String value) {
		String varValue = this.params.get(name);
		if (varValue != null && !varValue.equals(value)) {
			throw new RuntimeException("variable " + name + " has been defined already");
		}
		this.params.put(name, value);
	}
	
	public File getOutputFile(String basename) {
		basename = basename.replaceAll("[\\\\/:*?\"<>|]", "_");
		return new File(getOutputDir() + "/" + basename);
	}
	
	public String parseParamValue(CaseFinder caseFinder, String paramValue) {
		return paramProducerFactory.parseParamValue(caseFinder, paramValue);
	}
}
