package ca.loobo.restbot.validators;

import java.io.IOException;

import ca.loobo.restbot.Case;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.load.Dereferencing;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

public class ResponseSchemaValidator implements CaseValidator {

	@Override
	public int validate(Case c) {

		try {
			if (c.getResponseSchema() != null) {
		        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
		                .dereferencing(Dereferencing.INLINE).freeze();
	            final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
		                .setLoadingConfiguration(cfg).freeze();

				JsonSchema schema = factory.getJsonSchema(JsonLoader
						.fromString(c.getResponseSchema()));
				JsonNode jsonResponse = JsonLoader.fromString(c.getRawResponse());
				ProcessingReport report = schema.validate(jsonResponse);
				if (!report.isSuccess()) {
					c.addError(report.toString());
					return BLOCK;
				}
			}
		} catch (IOException e) {
			c.addError(e.getMessage());
			return BLOCK;
		} catch (ProcessingException e) {
			c.addError(e.getMessage());
			return BLOCK;
		}

		return CONTINUE;
	}

}
