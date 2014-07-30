package ca.loobo.restbot.param;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.loobo.restbot.param.DBValueParamProducer;

public class DBValueParamProducerTest {

	@Test
	public void testConnection() {
		DBValueParamProducer p = new DBValueParamProducer(null, "jdbc:oracle:thin:@san-dev-ora-01:1521:DIT",
				"san_qpmtvx54_admin", "san_qpmtvx54_admin");
		
		String s = p.value(null, "db://AUTH_TOKEN_MAP/QUICKPLAY_ID?DEVICE_UNIQUE_ID=LB35839804ABCDEF");
		assertEquals("86ce7463-95aa-4463-8201-ee7d1cfb41cd_1403700758044", s);
	}
}
