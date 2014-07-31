package ca.loobo.restbot.utils;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.javacrumbs.jsonunit.JsonAssert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.load.Dereferencing;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

public class JsonValidationTest {


	public String loadString(String name) throws FileNotFoundException, IOException {
        String path = "src/test/resources/" + name + ".json";
        return IOUtils.toString(new FileInputStream(new File(path)));

	}

	public JsonNode loadJsonNode(String name) throws FileNotFoundException, IOException {
        return JsonLoader.fromString(loadString(name));
	}
	
	
	public void assertValid(String schemaName, String responseName) throws FileNotFoundException, IOException, ProcessingException {
        final JsonNode fstabSchema = loadJsonNode(schemaName);
        final JsonNode good = loadJsonNode(responseName);

        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .dereferencing(Dereferencing.INLINE).freeze();
        final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
            .setLoadingConfiguration(cfg).freeze();

        final JsonSchema schema = factory.getJsonSchema(fstabSchema);

        ProcessingReport report;

        report = schema.validate(good);
        
        assertTrue(report.toString(), report.isSuccess());
	}
	
	public void assertEquals(String templateFileName, String responseFileName) throws FileNotFoundException, IOException {

		JsonAssert.assertJsonEquals(loadString(templateFileName), loadString(responseFileName)); 


	}

	
	@Test
	public void test_titles() throws IOException, ProcessingException {
//		assertValid("schema_titles", "response_titles");
	}
	

	// use "${json-unit.ignore}" in template file to ignore fields 
	// which are not required to compare
	@Test
	public void test_compare() throws FileNotFoundException, IOException {
//		assertEquals("template_case_5_1.json", "category5_std_response.json");
	}
}
