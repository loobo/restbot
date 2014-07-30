package ca.loobo.restbot.param;

import ca.loobo.restbot.CaseFinder;

public interface ParamProducer {

	public String value(CaseFinder finder, String valueUrl);
}
