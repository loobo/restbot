package ca.loobo.restbot.param;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**

 * @author robertx
 *
 */
public class RangeValueParamProducer {

	public final static String PATTERN = "^\\$?\\[([ a-zA-Z0-9,-]+)\\]$";	

	static Pattern pattern = Pattern.compile(PATTERN);
	
	public static LinkedList<String> value(String expression) {
		Matcher m = pattern.matcher(expression);
		
		if (!m.matches() || m.groupCount()!=1) {
			return null;
		}
		
		String valList = m.group(1);
		LinkedList<String> vals = new LinkedList<String>();
		if (Pattern.matches("[0-9]+-[0-9]+", valList)) {
			String[] pair = valList.split("-");
			int low = Integer.valueOf(pair[0]);
			int high = Integer.valueOf(pair[1]);
			for(int i=low; i<=high; i++) {
				vals.add(String.valueOf(i));
			}
		}
		else {
			String[] strs = valList.split(",");
			for(String v : strs) {
				vals.add(v.trim());
			}
		}
		
		return vals;
	}
	
	public static boolean matches(String paramValue) {
		return pattern.matcher(paramValue).matches();
	}
}
