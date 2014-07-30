package ca.loobo.restbot.validators;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.loobo.restbot.Case;

import com.jayway.jsonpath.JsonPath;

public class SimpleValueValidator extends AbstractValueValidator {

	public SimpleValueValidator() {
		super("[^/]+");
	}

	public void doCheck(Object value, String jsonPath,	String expectedValuePattern, Case ac) {

		// must it be absent ?
		if (ValuePattern.mustAbsent(expectedValuePattern)) {
			if (value != null) {
				if (!shouldReturnArray(expectedValuePattern) || !isEmptyArray(value)) {
					String info = String.format(
							"    path=%s expectation: null got: %s", jsonPath,
							value);
					ac.addError(info);
					return;
				}
			}

			// Good, the value is absent
			return;
		}

		if (ValuePattern.mustExist(expectedValuePattern)) {
			if (value == null || (shouldReturnArray(expectedValuePattern) && isEmptyArray(value))) {
				String info = String.format(
						"    path=%s expectation: not null got: null", jsonPath);
				ac.addError(info);
				return;
			}

			// Good, the value is absent
			return;
		}
		
		// false if absent
		if (value == null) {
			String info = String.format("    path=%s expectation:[%s] got: null", jsonPath,	expectedValuePattern);
			ac.addError(info);
		}

		// is a collection object?
		if (value instanceof Collection) {
			checkCollectionValue(jsonPath, expectedValuePattern, value, ac);
			return;
		}

		if (value instanceof Double) {
			checkDoubleValue(jsonPath, expectedValuePattern, value, ac);
			return;
		}

		checkStringValue(jsonPath, expectedValuePattern, value, ac);

	}

	@Override
	void check(File jsonFile, String jsonPath, String valuePattern, Case ac) {

		try {
			Object value = JsonPath.read(jsonFile, jsonPath);
			doCheck(value, jsonPath, valuePattern, ac);
		} catch (IOException e) {
			ac.addError(e.getMessage());
		}
	}

	@Override
	public void check(String json, String jsonPath, String valuePattern, Case ac) {
		Object value = null;
		if (json != null) {
			value = JsonPath.read(json, jsonPath);
		}
		doCheck(value, jsonPath, valuePattern, ac);		
	}

	private boolean shouldReturnArray(String jsonPath) {
		return jsonPath.contains("..");
	}
	

	private boolean isEmptyArray(Object val) {
		return val != null && val instanceof JSONArray && ((JSONArray)val).length()==0;
	}

}
