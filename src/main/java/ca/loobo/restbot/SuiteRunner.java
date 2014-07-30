package ca.loobo.restbot;

import org.junit.runners.model.InitializationError;

import ca.loobo.restbot.reader.ExcelReader;

public class SuiteRunner extends AbstractSuiteRunner {

	public SuiteRunner(Class<?> testClass)
			throws InitializationError {
		super(testClass, new ExcelReader());
	}

}
