package ca.loobo.restbot.validators;

import java.io.File;
import java.util.Collection;

import ca.loobo.restbot.Case;

public abstract class AbstractValueValidator implements ValueValidator {

	private String jsonPathPattern;
	
	public AbstractValueValidator(String jsonPathPattern) {
		this.jsonPathPattern = jsonPathPattern;
	}
	
	public boolean accept(String jsonPath) {
		return jsonPath.matches(jsonPathPattern);
	}
	
	abstract void check(File jsonFile, String jsonPath, String valuePattern, Case ac);
	public abstract void check(String json, String jsonPath, String valuePattern, Case ac);
	
	@SuppressWarnings("unchecked")
	protected void checkCollectionValue(String jsonPath,
			String expectedValuePattern, Object values, Case ac) {
		for (Object v : (Collection<Object>) values) {
			if (!v.toString().matches(expectedValuePattern)) {
				String info = String.format(
						"    path=%s expectation: [%s] got: [%s]", jsonPath,
						expectedValuePattern, v == null ? "NULL" : v);
				ac.addError(info);
			}
		}
	}

	protected void checkDoubleValue(String jsonPath, String expectedValuePattern,
			Object value, Case ac) {
		Double dval = (Double) value;
		if (dval != Double.parseDouble(expectedValuePattern)) {
			String info = String.format(
					"    path=%s expectation: [%s] got: [%s]", jsonPath,
					expectedValuePattern, value);
			ac.addError(info);
		}
	}
	
	protected void checkStringValue(String jsonPath, String expectedValuePattern,
			Object value, Case ac) {
		if (expectedValuePattern == null) {
			ac.addError("expectedValuePattern is null");
			return;
		}
		
		if (value == null) {
			String info = String.format(
					"    path=%s expectation: [%s] got: null", jsonPath,
					expectedValuePattern);
			ac.addError(info);
			return;
		}
		
		if (!value.toString().matches("^"+expectedValuePattern+"$")) {
			String info = String.format(
					"    path=%s expectation: [%s] got: [%s]", jsonPath,
					expectedValuePattern, value);
			ac.addError(info);
		}
	}
}
