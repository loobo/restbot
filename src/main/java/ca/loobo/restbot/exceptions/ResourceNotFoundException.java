package ca.loobo.restbot.exceptions;

@SuppressWarnings("serial")
public class ResourceNotFoundException extends RuntimeException {
	
	public ResourceNotFoundException(String filePath) {
		super("couldn't read file " + filePath);
	}

}
