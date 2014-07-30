package ca.loobo.restbot.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class CaseNotExecutedException extends RuntimeException {
	final static Logger logger = LoggerFactory.getLogger(CaseNotExecutedException.class);
	final private String caseId;
	
	public CaseNotExecutedException(String caseId) {
		super("case " + caseId + " haven't been executed");
		this.caseId = caseId;
		logger.debug("{} hasn't been executed", caseId);
	}

	public CaseNotExecutedException(String msg, String caseId) {
		super(msg);
		this.caseId = caseId;
		logger.debug("{} hasn't been executed", caseId);
	}
	
	public String getCaseId() {
		return this.caseId;
	}
}
