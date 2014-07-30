package ca.loobo.restbot.param;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.loobo.restbot.Case;
import ca.loobo.restbot.CaseFinder;
import ca.loobo.restbot.Context;
import ca.loobo.restbot.exceptions.CaseNotExecutedException;

/**
 * $result://case_no/response_json_path
 * @author robertx
 *
 */
public class ParamReuseParamProducer extends URIParamProducer {
	final static Logger logger = LoggerFactory.getLogger(ParamReuseParamProducer.class);

	public ParamReuseParamProducer(Context context) {
		super(context);
	}
	
	@Override
	public String value(CaseFinder finder, URI valueUri) {
		
		String caseId = valueUri.getAuthority();
		String paramName = valueUri.getPath().substring(1);
		
		Case c = finder.getCase(caseId);
		logger.trace("looking for case {}", caseId);
		if (c.getRawResponse() == null) {
			throw new CaseNotExecutedException(caseId);
		}
		return c.getQueryParams().get(paramName);
	}
}
