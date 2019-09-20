package com.rcs.service;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.rcs.DefaultThreadFactory;
import com.rcs.RCSSendServer;

import ib.ts.config.Config;
import ib.ts.config.RCSInfo;

@Component
public class RCSSendService {
	private static final Logger LOGGER = LoggerFactory.getLogger(RCSSendService.class);
	private ThreadPoolExecutor threadPool;
	private Thread sendThread;
	private RCSSendThread rcsSendThread;
	
	@PostConstruct
	public void init(){
		String tsCode = RCSSendServer.tsCode;
		MDC.put("discriminator", String.format("rcssend_%s", tsCode));
		
		RCSInfo rcsInfo = Config.getRcsInfo(RCSSendServer.tsCode);
		LOGGER.info("ThreadPool info CoreSize {} MaxPoolSize {} KeepAliveTime {}",
				rcsInfo.getThreadPoolCoreSize(), rcsInfo.getThreadMaxPoolSize(), rcsInfo.getThreadKeepAliveTime());
		
		threadPool = new ThreadPoolExecutor(rcsInfo.getThreadPoolCoreSize(), rcsInfo.getThreadMaxPoolSize(), rcsInfo.getThreadKeepAliveTime(),
				TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new DefaultThreadFactory("RCSSendService"));
		
		sendThread = new Thread(rcsSendThread = new RCSSendThread(threadPool, tsCode));
		sendThread.start();
	}
	
	@PreDestroy
	public void destroy() throws InterruptedException{
		sendThread.interrupt();
		while (!sendThread.isInterrupted() || !sendThread.isAlive()) {
			if (!sendThread.isAlive())
				break;
			LOGGER.info("Awaiting completion of sendThread." + sendThread.getState());
			Thread.sleep(1*1000);
		}		
		Thread.sleep(5*1000);
		rcsSendThread.destroy();
		
		threadPool.shutdown();
		/*while (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) { //optional *
			LOGGER.info("Executor did not terminate in the specified time.(30sec)"); //optional *
		}*/
		

		LOGGER.info("RCSSendService Stop Complted");
	}
}
