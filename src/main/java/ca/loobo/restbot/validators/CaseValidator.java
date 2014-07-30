package ca.loobo.restbot.validators;

import ca.loobo.restbot.Case;


public interface CaseValidator {
	final int CONTINUE = 0;
	final int BLOCK = 1;
	
	/**
	 * 
	 * @param c
	 * @return
	 * 		CONTINUE:  subsequent validators will be executed
	 * 		BLOCK:	subsequent validators will not be executed
	 */
	public int validate(Case c);
}
