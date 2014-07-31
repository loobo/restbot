package ca.loobo.restbot.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import ca.loobo.restbot.Case;
import ca.loobo.restbot.validators.NestedValueValidator;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;

public class JsonPathTest {

	@Test
	public void test() throws IOException {
		File jsonFile = new File("C:/Work/projects/catalog-api-test/src/main/resources/std_responses/case21_response.json");
		Object value = JsonPath.read(jsonFile, "$..titles.people");

		System.err.println(new Gson().toJson(value));
	}
	
	@Test
	public void test1() throws IOException {
		String jsonPath = "$..titles.people/$..name[*]";
		File jsonFile = new File("C:/Work/projects/catalog-api-test/src/main/resources/std_responses/case21_response.json");

		NestedValueValidator checker = new NestedValueValidator();
		assertTrue(checker.accept(jsonPath));
		Case ac = new Case(null);
		checker.check(jsonFile, jsonPath, "Jennifer Love Hewitt", ac);
		for(String err : ac.getErrors()) {
			System.err.println(err);
		}
		assertTrue(ac.getErrors().size()==0);
	}
	
}
