package ca.loobo.restbot;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.NDC;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import ca.loobo.restbot.exceptions.ParameterNotFoundException;
import ca.loobo.restbot.exceptions.PriorCaseNotExecutedException;
import ca.loobo.restbot.validators.CaseValidator;
import ca.loobo.restbot.validators.ResponseExpectionValidator;
import ca.loobo.restbot.validators.ResponseSchemaValidator;
import ca.loobo.restbot.validators.ResponseTemplateValidator;

public class SingleCaseRunner extends CaseRunner {
	private static Logger logger = LoggerFactory.getLogger(SingleCaseRunner.class);
	
	//checker have to run in order
	private List<CaseValidator> responseCheckers = Arrays.asList(
			new ResponseSchemaValidator(),
			new ResponseTemplateValidator(),
			new ResponseExpectionValidator());
	
	final Description description;
	static int index = 0;
	final Context context;
	
	public SingleCaseRunner(Context ctx, Case c) {
		super(c);
		this.context = ctx;
		description = Description.createTestDescription("TVSB_Test", c.getId().toString());
	}
	
	@Override
	public Description getDescription() {
		return this.description;
	}

	@Override
	public void run(RunNotifier notifier) {
		Assert.notNull(notifier);
		
		try {
			logger.debug("--- BEGIN Test {} ---", mCase.getId());
			NDC.push("\t");
			notifier.fireTestStarted(getDescription());
			if (execute(mCase)) {
				for(CaseValidator checker : responseCheckers) {
					if (checker.validate(mCase) == CaseValidator.BLOCK) {
						break;
					}
				}
			}
			
			this.context.addFinishedCase(mCase);
			
			if (mCase.getErrors().size()>0) {
				StringBuilder sb = new StringBuilder();
				sb.append("Case Description: ");
				sb.append(mCase.getDescription()).append("\n");
				for(String info : mCase.getInfos()) {
					sb.append("    ").append(info).append("\n");
				}
				sb.append("Errors: \n");
				for(String err : mCase.getErrors()) {
					sb.append("    ").append(err).append("\n");
				}
				notifier.fireTestFailure(new Failure(getDescription(), new AssertionError(sb.toString())));
			}
		} catch(PriorCaseNotExecutedException e) {
			notifier.fireTestFailure(new Failure(getDescription(), new AssumptionViolatedException(e.toString())));
		}
		
		finally {
			notifier.fireTestFinished(getDescription());
			NDC.clear();
			logger.debug("--- END Test {} ---\n\n", mCase.getId());
		}

	}
	
	private boolean execute(Case c) {
		try {
			if (c.isGroupCase()) {
				List<ChildCase> cases = c.childs();
				for(ChildCase cc : cases) {
					logger.debug("requesting {} ", cc.getUrl());
					c.addInfo("url: " + cc.getUrl());
					String response = new RestClient().getForString(cc.getUrl());
					if (response == null) {
						c.addError("couldn't get any response");
						return false;
					}
				}
				c.setRawResponse("");
			}
			else {
				logger.debug("requesting {}", c.getUrl());
				c.addInfo("url: " + c.getUrl());
				String response = new RestClient().getForString(c.getUrl());
				if (response == null) {
					c.addError("couldn't get any response");
					return false;
				}
				c.setRawResponse(response);
			}

		} catch (HttpClientErrorException e) {
			MediaType mediaType = e.getResponseHeaders().getContentType();
			if (!mediaType.includes(MediaType.APPLICATION_JSON)) {
				c.addError(e.getMessage());
				return false;				
			}
			c.setRawResponse(e.getResponseBodyAsString());
		} catch (HttpServerErrorException e) {
			c.addError(e.getMessage());
			return false;
		} catch (ResourceAccessException e) {
			c.addError(e.getMessage());
			return false;
		} catch (ParameterNotFoundException e) {
			c.addError(e.getMessage());
			return false;
		}

		c.setExecuted(true);
		return true;
	}
}
