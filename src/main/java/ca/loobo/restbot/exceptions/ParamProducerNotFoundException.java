package ca.loobo.restbot.exceptions;

@SuppressWarnings("serial")
public class ParamProducerNotFoundException extends RuntimeException {

	public ParamProducerNotFoundException(String paramValue) {
		super("not registered ParamProducer was found for param: " + paramValue);
	}

}
