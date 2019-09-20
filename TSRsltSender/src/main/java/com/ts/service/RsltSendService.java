package com.ts.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ts.DefaultThreadFactory;



@Component
public class RsltSendService {
	private static final Logger LOGGER = LoggerFactory.getLogger(RsltSendService.class);
	private ExecutorService workerPool;
	

	@PostConstruct
	public void init(){
		workerPool = Executors.newCachedThreadPool(new DefaultThreadFactory("RsltSendService"));
		workerPool.execute(new RsltSendThread());
	}
	
	@PreDestroy
	public void destroy() throws InterruptedException{
		workerPool.shutdown();
		if (!workerPool.awaitTermination(1, TimeUnit.SECONDS)) { 
			LOGGER.info("Executor did not terminate in the specified time.(30sec)"); 
			List<Runnable> droppedTasks = workerPool.shutdownNow();																
			if (!workerPool.awaitTermination(2, TimeUnit.SECONDS)) {
				LOGGER.warn("Pool did not terminate");
			}
		}
		LOGGER.info("RsltSender Stop Complted");
	}
}
