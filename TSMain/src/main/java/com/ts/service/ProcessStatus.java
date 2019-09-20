package com.ts.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import ib.db.MysqlSessionDAO;
import ib.db.MysqlSessionFactory;
import ib.db.schema.TSRsInfo;
import ib.ts.config.Config;

public class ProcessStatus implements Runnable{
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessStatus.class);
	//private static String javaopt = "java -jar  -Xms1024m -Xmx1024m -Dconf=./conf/config.properties";
	
	private static String tsDBUrl = Config.getTSDBUrl();
	private static String tsDBUser = Config.getTSDBUser();
	private static String tsdbPassword = Config.getTSDBPassword();
	
	private String receiverName = Config.getReceiverName();
	private String rcsSenderName = Config.getRCSSenderName();
	private String dbManName = Config.getDBManagerName();
	private String rsltSenderName = Config.getRsltSenderName();
	
	private Map<String, ProcessHandler> processes;
	
	public ProcessStatus() {
		// TODO Auto-generated constructor stub
		processes = new HashMap<String, ProcessHandler>();
	}
	
	public List<TSRsInfo> getRsInfos(){
		List<TSRsInfo> rsInfos = new ArrayList<TSRsInfo>();
		BasicDataSource source = new BasicDataSource();
		source.setDriverClassName("com.mysql.jdbc.Driver");
	    source.setUrl(tsDBUrl);
	    source.setUsername(tsDBUser);
	    source.setPassword(tsdbPassword);
	    LOGGER.info("RCS DB {}", tsDBUrl);
	    
	    MysqlSessionFactory mySqlSessionFactory = null;
		
	    try {
	    	mySqlSessionFactory = new MysqlSessionFactory(source);
	    	mySqlSessionFactory.getSqlSessionFactoryBean().setMapperLocations(new PathMatchingResourcePatternResolver().getResources("mapper.xml"));
			MysqlSessionDAO mysqlSessionDAO = mySqlSessionFactory.getSqlSessionTemplate().getMapper(MysqlSessionDAO.class);

			rsInfos =  mysqlSessionDAO.selectTSRsInfo(1002);
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
	    
	    return rsInfos;
	}
	public void startProcess(String cmd){
		ProcessHandler processHandler = new ProcessHandler(cmd);
		processes.put(cmd, processHandler);
		processHandler.start();
	}
	public void run() {
		// TODO Auto-generated method stub
		String cmd = null;
		cmd = String.format("%s", receiverName);
		startProcess(cmd);
		LOGGER.info("RCSReceiver is Started {}", cmd);
		
		cmd = String.format("%s R", receiverName);
		startProcess(cmd);
		LOGGER.info("RsltReceiver is Started {}", cmd);
	
		cmd = String.format("%s", dbManName);
		startProcess(cmd);
		LOGGER.info("TSDBManager is Started {}", cmd);
		
		for (String key : Config.getRcsInfos().keySet()){
			cmd = String.format("%s %s", rcsSenderName, key);
			startProcess(cmd);
			LOGGER.info("RCSSender is Started {}", cmd);
		}
	
		List<TSRsInfo> rcsInfos = getRsInfos();
		Iterator<TSRsInfo> itRcsInfos = rcsInfos.iterator();
		while(itRcsInfos.hasNext()){
			TSRsInfo rsinfo = itRcsInfos.next();
			String rs = rsinfo.getRsIp()+":"+rsinfo.getRsPort();
			cmd = String.format("%s %s", rsltSenderName, rs);
			startProcess(cmd);
			LOGGER.info("RsltSender is Started {}", cmd);
		}
		
		while(!Thread.interrupted()){
			
			for (String key : processes.keySet()){
				if (!processes.get(key).getProcess().isAlive()){
					LOGGER.info("Restart {}", key);
					startProcess(key);
				}
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LOGGER.error("error", e);
				break;
			}
		}
		destroy();
	}
	public void destroy(){
		for (String key : processes.keySet()){
			LOGGER.info("Destroy Process {}", key);
			processes.get(key).getProcess().destroy();
		}
		processes.clear();
	}
	
	class ProcessHandler extends Thread{
		private Process process;
		private String cmd;

		public ProcessHandler(String cmd) {
			this.cmd = cmd;
			
			startProcess();
		}
		public void startProcess(){
			try {
				if (process != null){
					process.destroy();
					
				}
				process = Runtime.getRuntime().exec(cmd);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOGGER.error("Process is not Started. {}", cmd);
			}
		}
		
		public void run() {
		    try {
		        InputStreamReader inpStrd = new InputStreamReader(process.getInputStream());
		        BufferedReader buffRd = new BufferedReader(inpStrd);

		        while (buffRd.readLine() != null) {

		        }
		        buffRd.close();
		    } catch (Exception e) {
		        
		    }
		}

		public Process getProcess() {
			return process;
		}

		public void setProcess(Process process) {
			this.process = process;
		}
	}

}
