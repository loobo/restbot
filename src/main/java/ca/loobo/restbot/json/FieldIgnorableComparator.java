package ca.loobo.restbot.json;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;

public class FieldIgnorableComparator extends DefaultComparator {

	public FieldIgnorableComparator(JSONCompareMode mode) {
		super(mode);
	}

    @Override
    public void compareValues(String prefix, Object expectedValue, Object actualValue, JSONCompareResult result)
            throws JSONException {
        if (expectedValue instanceof String && ignorable(expectedValue.toString())) {
            return;
        } 
        
        super.compareValues(prefix, expectedValue, actualValue, result);
    }

    private boolean ignorable(String value) {
    	return value.equalsIgnoreCase("${json-unit.ignore}") || value.matches("@@[^@]+@@");
    }
}
