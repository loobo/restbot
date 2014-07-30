package ca.loobo.restbot.validators;


import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import ca.loobo.restbot.Case;
import ca.loobo.restbot.json.FieldIgnorableComparator;

public class ResponseTemplateValidator implements CaseValidator {

	FieldIgnorableComparator comparator = new FieldIgnorableComparator(JSONCompareMode.NON_EXTENSIBLE);
	@Override
	public int validate(Case c) {
		String template = c.getResponseTemplate();
		if (template == null) {
			return CONTINUE;
		}
		
		try {
			assertEquals(c.getResponseTemplate(), c.getRawResponse());	
			return CONTINUE;
		} catch(Throwable e) {
			c.addError("template assert error:" + e.getMessage());
		}

		return CONTINUE;
	}

	void assertEquals(String expected, String actual) throws JSONException {
//		net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals(expected, actual);	
		JSONAssert.assertEquals(expected, actual, comparator);
	}
}
