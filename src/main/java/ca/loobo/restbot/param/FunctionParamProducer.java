package ca.loobo.restbot.param;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.loobo.restbot.CaseFinder;
import ca.loobo.restbot.functions.Function;
import ca.loobo.restbot.functions.RandomValueFunction;

public class FunctionParamProducer implements ParamProducer {

	public final static String PATTERN = "^\\$?([a-z]+)\\(([^)]+)\\)$";
	private static Map<String, Function> functions = new HashMap<String, Function>();
	
	static {
		functions.put("rands", new RandomValueFunction());
		functions.put("randi", new RandomValueFunction());
	}
	
	Pattern pattern = Pattern.compile(PATTERN);
	
	@Override
	public String value(CaseFinder finder, String expression) {
		Matcher m = pattern.matcher(expression);
		
		if (!m.matches() || m.groupCount()!=2) {
			return null;
		}
		
		String method = m.group(1);
		String param = m.group(2);

		Function f = functions.get(method);
		if (f != null) {
			return f.call(method, param);
		}
		
		return null;
	}

}
