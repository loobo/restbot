package ca.loobo.restbot;

import org.junit.runners.model.InitializationError;

/**
 * Usage: jar -jar restbot.jar [schemaFilePath]
 * 			schemaFilePath:   path of testcases file, default to classpath:vstd_resources/testcases.xlsx
 * @param args
 */
public class Main {
	private static String DEFAULT_TESTCASE_FILE = "classpath:vstb_resources/testcases.xlsx";

	public static void main(String[] args) throws InitializationError {
		SuiteRunner r;
		try {
			r = new SuiteRunner(Main.class);
		} catch (InitializationError e) {
			e.printStackTrace();
			return;
		}

		if (args.length == 0) {
//			System.out.println("Usage: restbot schemaFilePath");
			r.addResource(DEFAULT_TESTCASE_FILE);
		}
		else {
			r.addFileResource(args[0]);
		}
		
		r.start();
		System.exit(r.getContext().getResult().getFailedNumber());
	}
	
}
