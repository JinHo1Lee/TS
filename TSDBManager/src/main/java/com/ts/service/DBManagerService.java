package com.ts.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ts.DefaultThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import ib.ts.config.Config;
import ib.ts.config.RCSInfo;

@Component
public class DBManagerService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DBManagerService.class);
	private ExecutorService workerPool;
	
	@PostConstruct
	public void init(){
		workerPool = Executors.newCachedThreadPool(new DefaultThreadFactory("TSDBManager"));
		LOGGER.info("TSDBManager Server Init");
		
		loadConfig();
	}
	
	@PreDestroy
	public void serverDestroy() throws InterruptedException {
		
		workerPool.shutdown();
		if (!workerPool.awaitTermination(1, TimeUnit.SECONDS)) { 
			LOGGER.info("Executor did not terminate in the specified time.(30sec)"); 
			List<Runnable> droppedTasks = workerPool.shutdownNow();		
			if (!workerPool.awaitTermination(2, TimeUnit.SECONDS)) {
				LOGGER.warn("Pool did not terminate");
			}
		}
		Thread.sleep(1000*3);
		
		workerPool.shutdown();
		LOGGER.info("TSDBManager Stop Complted");
	}
	
	public void loadConfig(){
		for (int i=1; i<=Config.getDBQueCnt(); i++){
			workerPool.execute(new DBManagerThread(i));
		}

		new Thread(new TimeOutThread()).start();
	}
}
