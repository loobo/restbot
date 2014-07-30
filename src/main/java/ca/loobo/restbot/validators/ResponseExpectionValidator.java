package ca.loobo.restbot.validators;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.PatternSyntaxException;

import ca.loobo.restbot.Case;
import ca.loobo.restbot.Context;

public class ResponseExpectionValidator implements CaseValidator{
	List<AbstractValueValidator> checkers = Arrays.asList(new SimpleValueValidator(), new NestedValueValidator());

	@Override
	public int validate(Case c) {

		for (Entry<String, String> entry : c.getExpectations().entrySet()) {
			String jsonPath = entry.getKey();
			String value = entry.getValue();
			
			String expectedValuePattern = parseValue(c.getContext(), value);
			boolean requiredToCheck = ValuePattern.allowAnyValue(expectedValuePattern);
			// not need to check if any value is allowed
			if (!requiredToCheck) {
				continue;
			}
			
			try {
				AbstractValueValidator checker = findChecker(jsonPath);
				checker.check(c.getRawResponse(), jsonPath, expectedValuePattern, c);
			} catch(PatternSyntaxException e) {
				e.printStackTrace();
				c.addError(e.getMessage());
				return BLOCK;
			}
			catch (Exception e) {
				if (requiredToCheck) {
					String info = String.format(
							"    path=%s expectation:%s got: <Not Found>", jsonPath,
							expectedValuePattern);
					c.addError(info);
				}
			} 
		}
		
		return CONTINUE;
	}
	
	private AbstractValueValidator findChecker(String jsonPath) {
		for(AbstractValueValidator checker : checkers) {
			if (checker.accept(jsonPath)) {
				return checker;
			}
		}
		
		return null;
	}

	public String parseValue(Context context, String value) {
		return context.parseParamValue(context, value);
	}

}
