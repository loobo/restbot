package ca.loobo.restbot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.loobo.restbot.param.RangeValueParamProducer;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Case extends AbstractCase {
	final static Logger logger = LoggerFactory.getLogger(Case.class);
	
	private Map<String, String> metas;						// meta info from resource file

	@JsonIgnore
	private HashMap<String, String> queryParams;	// used to generate the Url pattern

	@JsonIgnore
	private Map<String, String> expections;
	
	@JsonIgnore
	List<ChildCase> childCases;
	
	public Case(Context ctx) {
		super(ctx);
		this.metas = new HashMap<String, String>();
		this.queryParams = new HashMap<String, String>();
		this.expections = new HashMap<String, String>();
	}
	
	public List<ChildCase> childs() {
		if (childCases != null) {
			return childCases;
		}
		
		List<Map<String, String>> paramList = new LinkedList<Map<String, String>>();

		for (Entry<String, String> pair : getQueryParams().entrySet()) {
			String name = pair.getKey();
			List<String> paramValue = RangeValueParamProducer.value(pair.getValue());
			if (paramValue == null) {
				paramValue = Arrays.asList(pair.getValue());
			}

			List<Map<String, String>> newList = new LinkedList<Map<String, String>>();

			for (String value : paramValue) {
				if (paramList.size() == 0) {
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put(name, value.toString());
					newList.add(paramMap);
					continue;
				}
				for (Map<String, String> paramMap : paramList) {
					Map<String, String> newMap = new HashMap<String, String>(
							paramMap);
					newMap.put(name, value.toString());
					newList.add(newMap);
				}
			}

			paramList = newList;

		}

		childCases = new LinkedList<ChildCase>();
		int i=0;
		for(Map<String, String> params : paramList) {
			ChildCase cc = new ChildCase(getContext(), this, params, i++);
			childCases.add(cc);
		}
		
		return childCases;
	}

	@Override
	public Map<String, String> getMetas() {
		return this.metas;
	}

	@Override
	public Map<String, String> getExpectations() {
		return this.expections;
	}

	@Override
	public Map<String, String> getQueryParams() {
		return this.queryParams;
	}
		
}
