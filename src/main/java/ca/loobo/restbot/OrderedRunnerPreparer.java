package ca.loobo.restbot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.runner.Runner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import ca.loobo.restbot.exceptions.CaseNotExecutedException;
import ca.loobo.restbot.exceptions.CaseNotFoundException;

/**
 * A case depends on the others in 3 ways
 * 		1. declare in meta columns of Excel resource file 
 *         with title "dependencies" and value caseId1, caseId2
 *      2. refer to the other cases in parameter list
 *      3. refer to the other cases in expected response value list
 * @author robertx
 *
 */
public class OrderedRunnerPreparer implements CaseFinder {
	private final Logger logger = LoggerFactory.getLogger(OrderedRunnerPreparer.class);
	private LinkedHashMap <String, CaseRunner> runners = new LinkedHashMap <String, CaseRunner>();
	private Context context;
	
	public OrderedRunnerPreparer(Context context) {
		this.context = context;
	}
	
	public void prepare(Context context) {
		LinkedList<Case> pendings = new LinkedList<Case>(context.getCases());
		
		int i = 0;
		while(pendings.size()>0) {
			Case c = pendings.pop();
			try {
				checkDependencies(c);
				checkParams(c);
				checkExpections(c);
				CaseRunner r = new SingleCaseRunner(context, c);
				logger.debug("{} adding runner for {}", ++i, c.getId());
				this.runners.put(c.getId(), r);
			} catch(CaseNotFoundException e) {
				//throw it out if it is not in the orignal case set
				if (c.getContext().getCase(e.getCaseId()) == null) {
					throw new CaseNotFoundException(e.getCaseId());
				}
				
				pendings.addLast(c);
			}
		}		
	}
	
	public List<Runner> getRunners() {
		return new ArrayList<Runner>(runners.values());
	}
	
	private void checkParams(Case c) {
		for(String val : c.getQueryParams().values()) {
			try {
				context.parseParamValue(this, val);
			} catch(CannotGetJdbcConnectionException e) {
				//igore it, it is not related to the current running check
			} catch(CaseNotExecutedException e) {
				//igore it, it is not related to the current running check
			}
		}
	}
	
	private void checkExpections(Case c) {
		for(String val : c.getExpectations().values()) {
			try {
				context.parseParamValue(this, val);
			} catch(CannotGetJdbcConnectionException e) {
				//igore it, it is not related to the current running check			
			} catch(CaseNotExecutedException e) {
				//igore it, it is not related to the current running check
			}
		}		
	}
	
	private void checkDependencies(Case c) {
		
		for(String caseId : c.getDependencies()) {
			if (runners.get(caseId) == null) {
				throw new CaseNotFoundException(caseId);
			}
		}

	}

	@Override
	public Case getCase(String caseId) {
		return runners.get(caseId).getCase();
	}
}
