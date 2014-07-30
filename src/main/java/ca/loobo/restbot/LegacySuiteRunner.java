package ca.loobo.restbot;

import org.junit.runners.model.InitializationError;

import ca.loobo.restbot.reader.legacy.LegacyExcelReader;

public class LegacySuiteRunner extends AbstractSuiteRunner {

	public LegacySuiteRunner(Class<?> testClass)
			throws InitializationError {
		super(testClass, new LegacyExcelReader());
	}

}
