package ca.loobo.restbot.utils;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class JsonTest {

	static class MyJsonDeserializer implements JsonDeserializer<Long>{

		@Override
		public Long deserialize(JsonElement element, Type arg1,
				JsonDeserializationContext arg2) throws JsonParseException {
			return element.getAsLong();
		}
	}
	
	public static Gson excludeFieldsWithoutExposeAnnotation() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new MyJsonDeserializer());
	    
		Gson gson = gsonBuilder.excludeFieldsWithoutExposeAnnotation().create();
		return gson;
	}
	
	@Test
	public void test() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Long.class, new MyJsonDeserializer());
		gsonBuilder.registerTypeAdapter(Double.class, new MyJsonDeserializer());
		gsonBuilder.registerTypeAdapter(Float.class, new MyJsonDeserializer());
		gsonBuilder.registerTypeAdapter(Number.class, new MyJsonDeserializer());
		gsonBuilder.registerTypeAdapter(Integer.class, new MyJsonDeserializer());
        Gson gson = gsonBuilder.serializeNulls().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        Object obj = gson.fromJson("{id:1}", Object.class);
        System.err.println(gson.toJson(obj));
	}
	
	@Test
	public void jacksonTest() throws com.fasterxml.jackson.core.JsonParseException, JsonMappingException, IOException {
		String json = "{\"status\":6,\"systemTime\":1404398003861}";
		Object obj = new ObjectMapper().readValue(json, Object.class);
		assertNotNull(obj);
		
		json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		System.err.println(json);

	}
}
