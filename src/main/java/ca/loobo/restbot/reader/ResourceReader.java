package ca.loobo.restbot.reader;

import org.junit.runners.model.InitializationError;
import org.springframework.core.io.Resource;

import ca.loobo.restbot.Context;

public interface ResourceReader {

	void read(Context context, Resource resource) throws InitializationError;

	void readProperties(Context context, Resource resource) throws InitializationError;


}
