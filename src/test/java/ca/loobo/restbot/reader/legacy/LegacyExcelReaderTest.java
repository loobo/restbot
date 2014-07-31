package ca.loobo.restbot.reader.legacy;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runners.model.InitializationError;
import org.springframework.core.io.FileSystemResource;

import ca.loobo.restbot.Case;
import ca.loobo.restbot.Context;
import ca.loobo.restbot.reader.ResourceReader;
import ca.loobo.restbot.reader.legacy.LegacyExcelReader;

public class LegacyExcelReaderTest {

	//@Test
	public void test() throws InitializationError {
		Context ctx = new Context();
		ctx.setHost("test");
		ResourceReader rr = new LegacyExcelReader();
		rr.read(ctx, new FileSystemResource("resources/TQA_catalogRestApi_v5.4.xlsx"));
		rr.readProperties(ctx, new FileSystemResource("resources/TQA_catalogRestApi_v5.4_environmentParams.txt"));

		assertEquals(465, ctx.getCases().size());
		int i=0;
		for(Case c : ctx.getCases()) {
			System.err.println("handled " + ++i);
			try {
			c.getResponseTemplate();
			}catch(Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}
}
