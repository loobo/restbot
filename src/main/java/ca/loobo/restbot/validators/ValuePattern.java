package ca.loobo.restbot.validators;


public class ValuePattern {
	private static final String EXIST = "+";
	private static final String NOT_ABSENT = "-";

	public static boolean mustExist(String value) {
		return EXIST.equals(value);
	}
	
	public static boolean mustAbsent(String value) {
		return NOT_ABSENT.equals(value);
	}
	
	public static boolean allowAnyValue(String valuePattern) {
		return valuePattern!=null && valuePattern.trim().length()>0;
	}
}
