package ca.loobo.restbot.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class CaseNotFoundException extends RuntimeException{
	final static Logger logger = LoggerFactory.getLogger(CaseNotFoundException.class);
	private String caseId;
	public CaseNotFoundException(String caseId) {
		super("case " + caseId + " was not found");
		this.caseId = caseId;
		logger.error("{} was not found", caseId);
	}
	
	public String getCaseId() {
		return caseId;
	}
}
