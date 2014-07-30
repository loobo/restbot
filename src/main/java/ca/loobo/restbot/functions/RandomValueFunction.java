package ca.loobo.restbot.functions;

import org.apache.commons.lang.RandomStringUtils;

public class RandomValueFunction implements Function {

	@Override
	public String call(String method, String param) {
		int length = Integer.parseInt(param);

		String value = "";
		if ("rands".equals(method)) {
			value = RandomStringUtils.randomAlphanumeric(length);
		}
		else if ("randi".equals(method)) {
			value = RandomStringUtils.randomNumeric(length);
		}
		
		return value;
	}

}
