package ca.loobo.restbot.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class PriorCaseNotExecutedException extends CaseNotExecutedException {
	final static Logger logger = LoggerFactory.getLogger(PriorCaseNotExecutedException.class);
	
	public PriorCaseNotExecutedException(String caseId) {
		super("prior case " + caseId + " haven't been executed", caseId);
		logger.debug("{} hasn't been executed", caseId);
	}

}
