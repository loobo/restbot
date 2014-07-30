package ca.loobo.restbot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Result {
	final static Logger logger = LoggerFactory.getLogger(Result.class);
	
	public ArrayList<Case> all = new ArrayList<Case>();
	public List<Case> passed = new LinkedList<Case>();
	public List<Case> failed = new LinkedList<Case>();
	public List<Case> blocked = new LinkedList<Case>();
	private Context context;
	
	public Result(Context context) {
		this.context = context;
	}
	
	public void add(Case c) {
		all.add(c);
		if (c.isPassed()) {
			this.passed.add(c);
		}
		else {
			this.failed.add(c);
		}
	}

	public ArrayList<Case> getAllCases() {
		return this.all;
	}
	
	public void log(FileWriter logWriter, Case a) throws IOException {
		logWriter.append(a.getId());
		logWriter.append("> ");
		logWriter.append(a.isPassed() ? "PASSED: " : "FAILED: ");
		logWriter.append(a.getUrl());
		logWriter.append("\n");
		for (String info : a.getErrors()) {
			logWriter.append(info);
			logWriter.append("\n");
		}
		logWriter.append(a.getRawResponse());
		logWriter.append("\n\n");
		logWriter.append("---------------------------------\n");
	}
	
	public void dump() throws IOException {
		StringBuilder dumpMsg = new StringBuilder();
		for(Case c : failed) {
			dumpMsg.append(String.format("-- FAILED TEST %s ----------\n", c.getId()));
			for(String s : c.getErrors()) {
				dumpMsg.append("\t");
				dumpMsg.append(s).append("\n");
			}
			dumpMsg.append("\n");
		}
		dumpMsg.append(String.format("TOTAL %d PASSED %d FAILED %d BLOCKED %d   ", 
				passed.size() + failed.size() + blocked.size(), passed.size(), failed.size(), blocked.size()));
		logger.info(dumpMsg.toString());

		FileWriter failedWriter = new FileWriter(context.getOutputFile("failed.json"));
		FileWriter passedWriter = new FileWriter(context.getOutputFile("passed.json"));
        passedWriter.write(toJson(passed));        
        passedWriter.close();
        for(Case c : failed) {
        	c.getId();
        	c.getResponse();
        }
        failedWriter.write(toJson(failed));
		failedWriter.close();
		
		for(Case c : all) {
			writeResponseToFile(c);
		}
	}

	public int getFailedNumber() {
		return failed.size();
	}
	
	private String toJson(Object obj) {
        String json="";
        
        //json = gson.toJson(obj);
		try {
			json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
	private void writeResponseToFile(Case c) {
		File f = context.getOutputFile("response_" + c.getId()+".json");
		try {
			if (c.getRawResponse() != null) {
				FileWriter w = new FileWriter(f);
				w.write(c.getRawResponse());
				w.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
