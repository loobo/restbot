package ca.loobo.restbot.param;

import java.net.URI;
import java.util.LinkedList;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import ca.loobo.restbot.CaseFinder;
import ca.loobo.restbot.Context;

public class DBValueParamProducer extends URIParamProducer {
	final static Logger logger = LoggerFactory.getLogger(DBValueParamProducer.class);
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	public DBValueParamProducer(Context context) {
		super(context);
		init();
	}
	
	public DBValueParamProducer(Context context, String url, String username, String password) {
		super(context);
		init(url, username, password);
	}

	
	private void init(String url, String username, String password) {
		if (jdbcTemplate == null) {
			dataSource = new SimpleDriverDataSource(new oracle.jdbc.driver.OracleDriver(), url, username, password);
			jdbcTemplate = new JdbcTemplate(dataSource);			
		}
	}
	
	private void init() {
		String url = context.getParam(Context.DB_URL);
		String username = context.getParam(Context.DB_USERNAME);
		String password = context.getParam(Context.DB_PASSWORD);
		init(url, username, password);
	}
	
	@Override
	public String value(CaseFinder finder, URI valueUrl) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ")
		.append(valueUrl.getPath().substring(1))
		.append(" FROM ")
		.append(valueUrl.getAuthority())
		.append(" WHERE ");
		String[] params = valueUrl.getQuery().split("&");
		LinkedList<String> pairs = new LinkedList<String>();
		for(String param : params) {
			String[] pair = param.split("=",2);
			pairs.add(pair[0] +"='" + parseParamValue(finder, pair[1]) + "'");
		}
		sql.append(StringUtils.join(pairs, " AND "));
		logger.debug(sql.toString());
		return jdbcTemplate.queryForObject(sql.toString(), String.class);
	}
	
	private String parseParamValue(CaseFinder finder, String val) {
		return context == null ? val :
		context.parseParamValue(finder, val);
	}
	
}
