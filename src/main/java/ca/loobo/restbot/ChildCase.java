package ca.loobo.restbot;

import java.util.Map;

public class ChildCase extends Case {

	final Case parent;
	final int seqNo;
	private Map<String, String> queryParams;	// used to generate the Url pattern

	public ChildCase(Context ctx, Case parent, Map<String, String> queryParams, int seqNo) {
		super(ctx);
		this.parent = parent;
		this.queryParams = queryParams;
		this.seqNo = seqNo;
	}
	
	public Map<String, String> getMetas() {
		return parent.getMetas();
	}
	public Map<String, String> getQueryParams() {
		return this.queryParams;
	}
	public Map<String, String> getExpectations() {
		return parent.getExpectations();
	}

	
}
