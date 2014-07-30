package ca.loobo.restbot.param;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.loobo.restbot.Case;
import ca.loobo.restbot.CaseFinder;
import ca.loobo.restbot.Context;
import ca.loobo.restbot.exceptions.PriorCaseNotExecutedException;

import com.jayway.jsonpath.JsonPath;

/**
 * $result://case_no/paramName
 * @author robertx
 *
 */
public class ResultReuseParamProducer extends URIParamProducer {
	final static Logger logger = LoggerFactory.getLogger(ResultReuseParamProducer.class);
	
	public ResultReuseParamProducer(Context context) {
		super(context);
	}
	
	@Override
	public String value(CaseFinder finder, URI valueUri) {
		String caseId = valueUri.getAuthority();
		String valuePath = valueUri.getPath().substring(1);
		logger.trace("valuePath {}", valuePath);
		Case c = finder.getCase(caseId);
		if (!c.isExecuted()) {
			throw new PriorCaseNotExecutedException(c.getId());
		}
		
		c.assertExecuted();
		return JsonPath.read(c.getRawResponse(), valuePath);

	}
}
