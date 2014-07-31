package ca.loobo.restbot.validators;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.Test;

import ca.loobo.restbot.validators.ResponseTemplateValidator;

public class ResponseTemplateCheckerTest {

	//@Test
	public void assertEqualsTest() throws FileNotFoundException, JSONException, IOException {
		ResponseTemplateValidator checker = new ResponseTemplateValidator();
		File template = new File("src/test/resources/template.json");
		File actual = new File("src/test/resources/actual.json");
		
		checker.assertEquals(
				IOUtils.toString(new FileInputStream(template)),
				IOUtils.toString(new FileInputStream(actual)));
	}
}
