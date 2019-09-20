package com.ts.service;

import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import ib.db.MysqlErrorHandler;
import ib.db.MysqlSessionDAO;
import ib.db.MysqlSessionFactory;
import ib.db.schema.TSRsInfo;
import ib.pdu.mfep.PDUConst;
import ib.pdu.mfep.PDUGwMt;
import ib.pdu.mfep.PDUReportReq;
import ib.pdu.util.FileQue;
import ib.pdu.util.NIOFileQue;
import ib.pdu.util.TSQueData;
import ib.pdu.util.TSQueData.TSQueUtil;
import ib.ts.config.Config;
import ib.ts.config.RCSInfo;

public class TimeOutThread implements Runnable{
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeOutThread.class);
	
	private String resultTableName;
	
	private static String tsDBUrl = Config.getTSDBUrl();
	private static String tsDBUser = Config.getTSDBUser();
	private static String tsdbPassword = Config.getTSDBPassword();
	
	private ObjectMapper mapper;
	private MysqlSessionFactory sessionFactory;
	private MysqlSessionDAO mysqlSessionDAO =null;
	private Map<String, String> tableMap;
	private Map<String, FileQue> reportQueMap;
	
	public TimeOutThread() {
		// TODO Auto-generated constructorstub
		
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		
		reportQueMap = new HashMap<String, FileQue>();
		tableMap = new HashMap<String, String>();
		Map<String, RCSInfo> rcsInfos = Config.getRcsInfos();
		for(String key : rcsInfos.keySet()){
			String carrierNet = String.valueOf(rcsInfos.get(key).getCarrierNet());
			if (!tableMap.containsKey(carrierNet)){
				resultTableName = "ts_rsltmap_"+carrierNet;
				tableMap.put(carrierNet, resultTableName);
				LOGGER.info("TimeOut table {}", resultTableName);
			}
		}
		
		loadConfig();
		
	}
	public void loadConfig(){
		BasicDataSource source = new BasicDataSource();
		source.setDriverClassName("com.mysql.jdbc.Driver");
	    source.setUrl(tsDBUrl);
	    source.setUsername(tsDBUser);
	    source.setPassword(tsdbPassword);
	    
	    try {
	    	sessionFactory = new MysqlSessionFactory(source);
			sessionFactory.getSqlSessionFactoryBean().setMapperLocations(new PathMatchingResourcePatternResolver().getResources("mapper.xml"));
			mysqlSessionDAO = sessionFactory.getSqlSessionTemplate().getMapper(MysqlSessionDAO.class);
			
			for (String key : reportQueMap.keySet()){
				reportQueMap.get(key).close();
			}
			
			List<TSRsInfo> tsRsInfos =  mysqlSessionDAO.selectTSRsInfo(1002);
			for (TSRsInfo tsRsInfo : tsRsInfos){
				String reportQueName = Paths.get(Config.getQueueDir()).toAbsolutePath()+"/"+tsRsInfo.getRsIp()+"."+tsRsInfo.getRsPort()+".rslt.que";
				LOGGER.info("Report Queue Info {}", reportQueName);
				FileQue reportQue = new NIOFileQue(reportQueName);
				reportQue.open();
				reportQueMap.put(tsRsInfo.getRsId(), reportQue);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("error", e);
		}
	}
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			if (Thread.currentThread().isInterrupted()){
				break;
			}
			
			try{
				for (String key : tableMap.keySet()){
					String resultTableName = tableMap.get(key);
					List<PDUReportReq> timeOutReq = mysqlSessionDAO.selectTimeOutResultMap(resultTableName);
					Iterator<PDUReportReq> itTimeout = timeOutReq.iterator();
					while(itTimeout.hasNext()){
						PDUGwMt pduGwMt = itTimeout.next();
						pduGwMt.setReportType(pduGwMt.getMsgType());
						pduGwMt.setIbRsltCode("2000");
						pduGwMt.setNetCode("");
						pduGwMt.setMsgStatus(TSQueUtil.TS_RECV_REPORT);
						pduGwMt.setTsRsltTime(System.currentTimeMillis()/1000);
						
						new DBManagerHandler(resultTableName, reportQueMap, mysqlSessionDAO).doResult(pduGwMt);
						LOGGER.info("Timeout {}", pduGwMt.toString());
					}
				}
			}catch(Exception e){
				LOGGER.error("error", e);
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Thread.currentThread().interrupt();
			}
		}
	}
}
