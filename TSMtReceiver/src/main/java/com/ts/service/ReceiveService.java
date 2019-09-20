package com.ts.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.ts.DefaultThreadFactory;
import com.ts.MtRecvServer;
import com.ts.handler.ReportHandler;

import ib.db.MysqlSessionDAO;
import ib.db.MysqlSessionFactory;
import ib.db.schema.TSRsInfo;
import ib.ts.config.Config;



@Component
public class ReceiveService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveService.class);
	private ExecutorService workerPool;
	
	private static String tsDBUrl = Config.getTSDBUrl();
	private static String tsDBUser = Config.getTSDBUser();
	private static String tsdbPassword = Config.getTSDBPassword();
	
	private List<Thread> reportThreads;
	
	
	@PostConstruct
	public void init(){
		//sendPool Init
		workerPool = Executors.newCachedThreadPool(new DefaultThreadFactory("ReceiveService"));
		
		reportThreads = new ArrayList<>();
		startService();
	}
	@PreDestroy
	public void destroy() throws InterruptedException{
		Iterator<Thread> itReportThread = reportThreads.iterator();
		while(itReportThread.hasNext()){
			Thread reportThread = itReportThread.next();
			reportThread.interrupt();
			while (!reportThread.isInterrupted() || !reportThread.isAlive()){
				if (!reportThread.isAlive())
					break;
					LOGGER.info("Awaiting completion of monitorThread.");
				Thread.sleep(1000 * 1);
			}
		}
		
		Thread.sleep(1000 * 3);
		
		workerPool.shutdown();
		if (!workerPool.awaitTermination(1, TimeUnit.SECONDS)) { 
			LOGGER.info("Executor did not terminate in the specified time.(30sec)"); 
			List<Runnable> droppedTasks = workerPool.shutdownNow();																
			if (!workerPool.awaitTermination(2, TimeUnit.SECONDS)) {
				LOGGER.warn("Pool did not terminate");
			}
		}
		LOGGER.info("ReceiveService Stop Complted");
	}
	
	public void startService(){
		if (MtRecvServer.opt.equals("R")){
			List<TSRsInfo> rsInfos = new ArrayList<TSRsInfo>();
			BasicDataSource source = new BasicDataSource();
			source.setDriverClassName("com.mysql.jdbc.Driver");
		    source.setUrl(tsDBUrl);
		    source.setUsername(tsDBUser);
		    source.setPassword(tsdbPassword);
		    
			MysqlSessionFactory mySqlSessionFactory = null;
			try {
		    	mySqlSessionFactory = new MysqlSessionFactory(source);
		    	mySqlSessionFactory.getSqlSessionFactoryBean().setMapperLocations(new PathMatchingResourcePatternResolver().getResources("mapper.xml"));
				MysqlSessionDAO mysqlSessionDAO = mySqlSessionFactory.getSqlSessionTemplate().getMapper(MysqlSessionDAO.class);
			
				rsInfos =  mysqlSessionDAO.selectTSRsInfo(2001);
				
				for (TSRsInfo rsinfo : rsInfos){
					MDC.put("discriminator", String.format("rsltrecv_%s", rsinfo.getRsIp()));
					Thread reportThread = new Thread(new ReportHandler(rsinfo.getRsIp()));
					reportThread.start();
					reportThreads.add(reportThread);
				}
			}catch(Exception e){
				LOGGER.error("error", e);
			}finally{
				try {
					if (mySqlSessionFactory != null)
						mySqlSessionFactory.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					LOGGER.error("error", e);
				}
			}
		}
		workerPool.execute(new ReceiveThread());
	}
}
