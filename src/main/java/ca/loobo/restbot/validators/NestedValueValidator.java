package ca.loobo.restbot.validators;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import ca.loobo.restbot.Case;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

public class NestedValueValidator extends AbstractValueValidator {

	
	public NestedValueValidator() {
		super(".+/[^/]+");
	}

	
	@SuppressWarnings("rawtypes")
	void doCheck(Object groups, String elementPath, String expectedValuePattern, Case ac) {
		if (!(groups instanceof Collection)) {
			ac.addError("Object type is not a collection");
			return;
		}
		
		for(Object elements : (Collection)groups) {
			try {
				String elementJson = new ObjectMapper().writeValueAsString(elements);
				checkValueInGroup(elementJson, elementPath, expectedValuePattern, ac);
			} catch (JsonProcessingException e) {
				ac.addError(e.getMessage());
			}
		}
	}
	
	// check at lease 1 value matching expectedValuePattern should be found in all elements matching elementPath
	@SuppressWarnings("rawtypes")
	void checkValueInGroup(String json, String elementPath, String expectedValuePattern, Case ac) {
		Object elements = JsonPath.read(json, elementPath);
		if (!(elements instanceof Collection)) {
			ac.addError("elements is not a collection");
			return;
		}

		boolean found = false;
		for (Object obj : (Collection) elements) {
			if (obj.toString().matches(expectedValuePattern)) {
				found = true;
				break;
			}
		}

		if (!found) {
			ac.addError(String.format("path: " + elementPath + "expections: contains [%s] + got []", expectedValuePattern ));
		}

	}


	@Override
	public void check(File jsonFile, String jsonPath, String valuePattern, Case ac) {
		String parts[] = jsonPath.split("/", 2);
		String groupPath = parts[0];
		String elementPath = parts[1];

		Object groups;
		try {
			groups = JsonPath.read(jsonFile, groupPath);
			doCheck(groups, elementPath, valuePattern, ac);
		} catch (IOException e) {
			ac.addError(e.getMessage());
		}
	}


	@Override
	public void check(String json, String jsonPath, String valuePattern, Case ac) {
		String parts[] = jsonPath.split("/", 2);
		String groupPath = parts[0];
		String elementPath = parts[1];

		Object groups = JsonPath.read(json, groupPath);
		doCheck(groups, elementPath, valuePattern, ac);

	}

}
