package ca.loobo.restbot;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface CaseElements {
	final static Logger logger = LoggerFactory.getLogger(CaseElements.class);
	final static String META_ID					= "id";
	final static String META_PATH 				= "path";
	final static String META_DESCRIPTION 		= "description";
	final static String META_RESPONSE_TEMPLATE 	= "responsetemplate";
	final static String META_RESPONSE_SCHEMA 	= "responseschema";
	final static String META_DEPENDENCIES 		= "dependencies";
	final static String META_COMMON_PARAMS		= "commonparams";
	final static String META_RESOURCE_FOLDER	= "resourcefolder";
	
	public Map<String, String> getQueryParams();
	public Map<String, String> getMetas();
	public Map<String, String> getExpectations();
		
}
