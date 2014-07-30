package ca.loobo.restbot;

import org.junit.runner.Runner;

public abstract class CaseRunner extends Runner {

	final protected Case mCase;
	public CaseRunner(Case c) {
		this.mCase = c;
	}
	
	public Case getCase() {
		return this.mCase;
	}
}
