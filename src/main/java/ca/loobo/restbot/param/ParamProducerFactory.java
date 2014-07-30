package ca.loobo.restbot.param;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.loobo.restbot.CaseFinder;
import ca.loobo.restbot.Context;
import ca.loobo.restbot.utils.StringUtils;

public class ParamProducerFactory {
	final static Logger logger = LoggerFactory.getLogger(ParamProducerFactory.class);
	
	Context context;
	private Map<String, ParamProducer> paramProducers = new HashMap<String, ParamProducer>();
	
	public ParamProducerFactory(Context context) {
		this.context = context;
		paramProducers.put("^\\$rands://.*", new RandomValueParamProducer(context));
		paramProducers.put("^\\$randi://.*", new RandomValueParamProducer(context));
		paramProducers.put("^\\$result://.*", new ResultReuseParamProducer(context));
		paramProducers.put("^\\$param://.*", new ParamReuseParamProducer(context));
		
		if (org.apache.commons.lang.StringUtils.isNotBlank(context.getParam(Context.DB_URL))) {
			paramProducers.put("^\\$db://.*", new DBValueParamProducer(context));
		}
		
		
		paramProducers.put(FunctionParamProducer.PATTERN, new FunctionParamProducer());				
	}
	
	public ParamProducer getProducer(String paramValue) {
		for(String patternTemplate : paramProducers.keySet()) {
			if (Pattern.matches(patternTemplate, paramValue)) {
				return paramProducers.get(patternTemplate);
			}
		}
		
		//throw new ParamProducerNotFoundException(paramValue);
		return null;
	}
	
	
	public String parseParamValue(CaseFinder finder, String paramValue) {
		//Ranged Value is not handled here
		if (!paramValue.startsWith("$") || RangeValueParamProducer.matches(paramValue)) {
			return paramValue;
		}
		
		String extendedValue = StringUtils.extend(paramValue, context.getParamMap());
		ParamProducer p = getProducer(extendedValue);
		if (p != null) {
			extendedValue = p.value(finder, extendedValue);
		}
		
		logger.debug(">> {} ==> {}", paramValue, extendedValue);
		if (!paramValue.equals(extendedValue)) {
			return parseParamValue(finder, extendedValue);
		}
		
		return extendedValue;
	}

}
