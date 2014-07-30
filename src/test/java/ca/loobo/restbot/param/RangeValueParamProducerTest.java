package ca.loobo.restbot.param;

import org.junit.Test;

import ca.loobo.restbot.param.RangeValueParamProducer;

public class RangeValueParamProducerTest {

	@Test
	public void test() {
		RangeValueParamProducer.value("$[1-5]");
	}
}
