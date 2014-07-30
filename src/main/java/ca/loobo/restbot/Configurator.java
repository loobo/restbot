package ca.loobo.restbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Configurator {
	static final Logger logger = LoggerFactory.getLogger(Configurator.class);
	
	private ApplicationContext applicationContext;
	private static Configurator instance = new Configurator();
	
	public static Configurator instance() {
		return instance;
	}
	
	private Configurator() {
			logger.info("Initializing Spring context.");

			this.applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");

			logger.info("Spring context initialized.");
	}
	
	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}
	

}
