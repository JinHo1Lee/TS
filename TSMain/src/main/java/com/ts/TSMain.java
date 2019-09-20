package com.ts;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.ts.service.ProcessStatus;


@Component
public class TSMain extends Thread{
	private static final Logger LOGGER = LoggerFactory.getLogger(TSMain.class);
	static ConfigurableApplicationContext context;
	
	public static void main(String[] args) {
		try {
			log4jSetting();
		} catch (Exception e) {
			System.out.println("-Dconf=config/config.properties");
			e.printStackTrace();
		}
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new TSMain());
		context = new ClassPathXmlApplicationContext(new String[] { "context.xml" });
		
		LOGGER.info("TSMain Started");
		
	}
	
	private static void log4jSetting() {
		// log4j.category.org.glassfish.jersey=WARN,stdout
		LogManager.getLogManager().reset();
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		java.util.logging.Logger.getLogger("global").setLevel(Level.FINEST);
	}
	
	@Override
	public void run() {
		if (context != null) {
			context.close();
		}
		LOGGER.info("TSMain nomarlly terminated");
	}
}
