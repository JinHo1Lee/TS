package com.rcs;

import java.util.logging.Level;
import java.util.logging.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.rcs.service.RCSSendService;
import com.rcs.service.RCSSendThread;

@Component
public class RCSSendServer extends Thread{
	private static final Logger LOGGER = LoggerFactory.getLogger(RCSSendServer.class);
	public static RCSSendServer server;
	static ConfigurableApplicationContext context;
	public static String tsCode;
	
	public static void main(String[] args) {
		
		try {
			log4jSetting();
		} catch (Exception e) {
			System.out.println("-Dconf=config/config.properties");
			e.printStackTrace();
		}
		if (args.length !=1){
			System.out.println("TsCode is null");
			System.exit(0);
		}
		tsCode = args[0];
		
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new RCSSendServer());
		context = new ClassPathXmlApplicationContext(new String[] { "context.xml" });
		
		LOGGER.info("RCS Communicator Started {}", tsCode);
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
		if (context != null){
			context.close();
		}
		LOGGER.info("RCS Communicator nomarlly terminated");
	}
}
