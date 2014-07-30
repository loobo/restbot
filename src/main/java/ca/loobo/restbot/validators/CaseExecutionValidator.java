package ca.loobo.restbot.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import ca.loobo.restbot.Case;
import ca.loobo.restbot.RestClient;

public class CaseExecutionValidator implements CaseValidator {
	static final Logger logger = LoggerFactory.getLogger(CaseExecutionValidator.class);

	@Override
	public int validate(Case c) {
		try {
			//TODO: check case dependency here
			logger.debug("testing {} {}", c.getId(), c.getUrl());

			String response = new RestClient().getForString(c.getUrl());
			if (response == null) {
				c.addError("couldn't get any response");
				return BLOCK;
			}
			c.setRawResponse(response);

		} catch (HttpClientErrorException e) {
			c.setRawResponse(e.getResponseBodyAsString());
		} catch (HttpServerErrorException e) {
			c.addError(e.getMessage());
			return BLOCK;
		} catch (ResourceAccessException e) {
			c.addError(e.getMessage());
			return BLOCK;
		} 

		c.setExecuted(true);
		return CONTINUE;
	}

}
