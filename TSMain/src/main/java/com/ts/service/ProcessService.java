package com.ts.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.ts.DefaultThreadFactory;

import ib.db.MysqlSessionDAO;
import ib.db.MysqlSessionFactory;
import ib.db.schema.TSRsInfo;
import ib.ts.config.Config;
import ib.ts.config.RCSInfo;

@Service
public class ProcessService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessService.class);
	private ExecutorService workerPool;	
	
	@PostConstruct
	public void init(){
		workerPool = Executors.newCachedThreadPool(new DefaultThreadFactory("ProcessService"));
		workerPool.execute(new ProcessStatus());
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
		
		LOGGER.info("TSMain Service Stop Complted");
	}
}