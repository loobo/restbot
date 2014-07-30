package ca.loobo.restbot.sample;

import org.junit.runner.RunWith;

import ca.loobo.restbot.SuiteRunner;
import ca.loobo.restbot.annotations.ResourceFiles;

@RunWith(SuiteRunner.class)
@ResourceFiles(
	value="file:resources/samplecase.xlsx"
//	, allows = { ".*_ts_.*" }
	)
//@Host(host="localhost", port=8082)
public class SampleTest {

}
