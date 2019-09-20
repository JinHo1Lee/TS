package com.ts.service;

import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PreDestroy;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.fabric.xmlrpc.base.Array;

import ib.db.schema.RCSResCode;
import ib.db.schema.TSRsInfo;
import ib.db.MysqlErrorHandler;
import ib.db.MysqlSessionDAO;
import ib.db.MysqlSessionFactory;
import ib.pdu.mfep.PDUConst;
import ib.pdu.mfep.PDUGwMt;
import ib.pdu.mfep.PDUReportReq;
import ib.pdu.util.TSQueData;
import ib.pdu.util.TSQueData.TSQueUtil;
import ib.ts.config.Config;
import ib.ts.config.RCSInfo;
import ib.pdu.util.ByteUtil;
import ib.pdu.util.FileQue;
import ib.pdu.util.IOFileQue;
import ib.pdu.util.NIOFileQue;

public class DBManagerThread implements Runnable{
	private static final Logger LOGGER = LoggerFactory.getLogger(DBManagerThread.class);
	
	private static String queDirName = Config.getQueueDir();
	
	private static String tsDBUrl = Config.getTSDBUrl();
	private static String tsDBUser = Config.getTSDBUser();
	private static String tsdbPassword = Config.getTSDBPassword();
	
	private long startTime;
	
	private String resultTableName;
	private HashMap<String, String> resultCodeMap;
	
	private FileQue dbQue;
	private ObjectMapper mapper;
	
	private Map<String, FileQue> reportQueMap;
	
	private MysqlSessionFactory sessionFactory;
	private MysqlSessionDAO mysqlSessionDAO =null;
	
	public DBManagerThread(int dbQueCnt) {
		// TODO Auto-generated constructor stub
		MDC.put("discriminator", "dbman");
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		
		Path path = Paths.get(queDirName);
		String tsidDirName = path.toAbsolutePath()+"/";
		String dbQueFullName = tsidDirName+"db.que."+dbQueCnt;
		LOGGER.info("DB Queue Info {}", dbQueFullName);
		dbQue = new NIOFileQue(dbQueFullName);
		try {
			dbQue.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("error",e);
		}
		reportQueMap = new HashMap<String, FileQue>();
	
		
		LOGGER.info("ts DB Url {}", tsDBUrl);
		LOGGER.info("ts DB User {}", tsDBUser);
		
		loadConfig();		
		Map<String, RCSInfo> rcsInfos = Config.getRcsInfos();
		for(String key : rcsInfos.keySet()){
			RCSInfo rcsInfo = rcsInfos.get(key);
			resultTableName = "ts_rsltmap_"+rcsInfo.getCarrierNet();
			mysqlSessionDAO.createResultMapTable(resultTableName);
		}
		startTime = System.currentTimeMillis()/1000;
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
			reportQueMap.clear();
			
			List<TSRsInfo> tsRsInfos =  mysqlSessionDAO.selectTSRsInfo(1002);
			for (TSRsInfo tsRsInfo : tsRsInfos){
				if (!reportQueMap.containsKey(tsRsInfo.getRsId())){
					String reportQueName = Paths.get(Config.getQueueDir()).toAbsolutePath()+"/"+tsRsInfo.getRsIp()+"."+tsRsInfo.getRsPort()+".rslt.que";
					LOGGER.info("Report Queue Info {}", reportQueName);
					FileQue reportQue = new NIOFileQue(reportQueName);
					reportQue.open();
					reportQueMap.put(tsRsInfo.getRsId(), reportQue);
				}
			}
			List<RCSResCode> resCodes = mysqlSessionDAO.getResCode();
			if(resultCodeMap != null)
				resultCodeMap.clear();
			
			Iterator<RCSResCode> itResCode = resCodes.iterator();
			resultCodeMap = new HashMap<String, String>();
			while(itResCode.hasNext()){
				RCSResCode resCode = itResCode.next();
				resultCodeMap.put(resCode.getCarrier()+":"+resCode.getResCode(), resCode.getIbRslt());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("error", e);
		}
	}
	
	public void run() {
		// TODO Auto-generated method stub
		TSQueData dbQueData =null;
		while(true){
			if (Thread.currentThread().isInterrupted()){
				break;
			}
			if (System.currentTimeMillis()/1000 - startTime >= Config.getReloadConfigTime()){
				loadConfig();
				startTime = System.currentTimeMillis()/1000;
			}
			
			byte[] buffer = null;
			String ibRslt;
			
			try {
				buffer = dbQue.deque();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOGGER.error("error", e);
			}
			
			if (buffer != null){
				dbQueData = null;
				try {
					dbQueData = mapper.readValue(buffer, TSQueData.class);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LOGGER.error("error", e);
				}
				
				PDUGwMt reportReq = dbQueData.getPduGwMt();
				MysqlErrorHandler mysqlErrorHandler;
				DBManagerHandler handler;
				
				switch(dbQueData.getStatus()){
				case TSQueUtil.TS_RECV_MT:
					resultTableName = "ts_rsltmap_"+reportReq.getCarrier();
					handler = new DBManagerHandler(resultTableName, reportQueMap, mysqlSessionDAO);
					handler.createLogTable(reportReq.getRsRecvTime());
					
					reportReq.setMsgStatus(TSQueUtil.TS_RECV_MT);
					try{
						mysqlErrorHandler = mysqlSessionDAO.insertResultMap(resultTableName, reportReq);			
						if(mysqlErrorHandler.getCode().equals("00000")){
							LOGGER.info("insert ResultMap {} {}", resultTableName, reportReq.toString());
						}else if (mysqlErrorHandler.getCode().equals("23000")){
							LOGGER.info("Duplication {} {}", resultTableName, reportReq.toString());
						}else{
							LOGGER.error("insert fail {} {} {}", reportReq.getCarrierKey(), reportReq.getRsMsgKey(), reportReq.getClientMsgKey());
							LOGGER.error("insert fail ReusltMap {} {} {}", resultTableName, mysqlErrorHandler.getCode(), mysqlErrorHandler.getMsg());
							
							try {
								dbQue.enque(mapper.writeValueAsBytes(dbQueData));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								Thread.currentThread().interrupt();
								LOGGER.error("error", e);
							}
						}
					}catch (Exception e){
						LOGGER.error("error", e);
					}
					break;
				case TSQueUtil.TS_SEND_ACK :
					resultTableName = "ts_rsltmap_"+reportReq.getCarrier();
					handler = new DBManagerHandler(resultTableName, reportQueMap, mysqlSessionDAO);
					
					reportReq.setMsgStatus(TSQueUtil.TS_SEND_ACK);		
					try{
						mysqlErrorHandler = mysqlSessionDAO.updateResultMap(resultTableName,
																			reportReq.getCarrierKey(),
																			reportReq.getMsgStatus(),
																			reportReq.getTsSendTime(),
																			reportReq.getTsCode(),
																			reportReq.getRsMsgKey(),
																			reportReq.getClientMsgKey(),
																			reportReq.getRecipients().get(0).getRecipientOrder(),
																			reportReq.getRecipients().get(0).getRecipientNum()
																			);
						if (mysqlErrorHandler.getCode().equals("00000")){
							LOGGER.info("update ResultMap {} {}", resultTableName, reportReq.toString());
						}else{
							LOGGER.error("update fail {}", reportReq.toString());
							LOGGER.error("update fail ReusltMap {} {} {} {}", resultTableName, mysqlErrorHandler.getCode(), mysqlErrorHandler.getMsg());
							try {
								dbQue.enque(mapper.writeValueAsBytes(dbQueData));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								Thread.currentThread().interrupt();
								LOGGER.error("error", e);							
							}
						}
					}catch (Exception e){
						LOGGER.error("error", e);
					}
					break;
				case TSQueUtil.TS_SEND_NAK:
					resultTableName = "ts_rsltmap_"+reportReq.getCarrier();
					handler = new DBManagerHandler(resultTableName, reportQueMap, mysqlSessionDAO);
					
					reportReq.setMsgStatus(TSQueUtil.TS_RECV_REPORT);
					ibRslt = reportReq.getIbRsltCode();
					if (ibRslt == null){
						ibRslt = resultCodeMap.get(getCarrierType(reportReq.getCarrierKey())+":"+reportReq.getNetCode());
						if (ibRslt == null || ibRslt.length()<=0){
							ibRslt = "3014";
						}
					}
					reportReq.setIbRsltCode(ibRslt);
					try {
						if (handler.doResult(reportReq)){
							
						}else{
							try {
								dbQue.enque(mapper.writeValueAsBytes(dbQueData));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								Thread.currentThread().interrupt();
								LOGGER.error("error", e);
							}
						}
					} catch (Exception e){
						LOGGER.error("error", e);
					}
					
					break;
				case TSQueUtil.TS_RECV_REPORT:
					try{
						int carrier = Integer.parseInt(reportReq.getCarrierKey().split("_")[0]);
						if (carrier != 20001 && carrier != 20002 && carrier != 20003){
							LOGGER.error("Unknown Carrier {}", reportReq.getCarrierKey());
							break;
						}
						resultTableName = "ts_rsltmap_"+carrier;
						handler = new DBManagerHandler(resultTableName, reportQueMap, mysqlSessionDAO);
						PDUGwMt resultMap = mysqlSessionDAO.selectResultMap(resultTableName, reportReq.getCarrierKey());
						ibRslt = resultCodeMap.get(getCarrierType(reportReq.getCarrierKey())+":"+reportReq.getNetCode());
						if (resultMap != null){
							resultMap.setReportType(resultMap.getMsgType());
						
							resultMap.setTsRsltTime(System.currentTimeMillis()/1000);
							resultMap.setMsgStatus(TSQueUtil.TS_RECV_REPORT);
							
							String netCode = reportReq.getNetCode();
							
							ibRslt = resultCodeMap.get(getCarrierType(reportReq.getCarrierKey())+":"+netCode);
							if (ibRslt == null || ibRslt.length()<=0){
								ibRslt = "3014";
							}
							resultMap.setNetCode(netCode);
							resultMap.setIbRsltCode(ibRslt);
							
							try {
								if (handler.doResult(resultMap)){
								
								}else{
									try {
										dbQue.enque(mapper.writeValueAsBytes(dbQueData));
									} catch (IOException e) {
										// TODO Auto-generated catch block
										Thread.currentThread().interrupt();
										LOGGER.error("error", e);
									}
								}
							} catch (Exception e){
								LOGGER.error("error", e);
							}
						}else{
							try{
								long timestamp = Long.parseLong(reportReq.getCarrierKey().split("_")[4]);
								Date date = new Date(timestamp*1000L); 
								SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.KOREA );
								String yymmdd = sdf.format(date);
								resultTableName = "ts_rsltmap_"+reportReq.getCarrierKey().split("_")[0]+"_"+yymmdd;
								PDUGwMt tmpResultMap = mysqlSessionDAO.selectResultMap(resultTableName, reportReq.getCarrierKey());
								if (tmpResultMap == null){
									if (System.currentTimeMillis()/1000 - timestamp < 86400){
										LOGGER.error("Reverse re-enque {} {}", resultTableName, reportReq.getCarrierKey());
										try {
											dbQue.enque(mapper.writeValueAsBytes(dbQueData));
										} catch (IOException e) {
											// TODO Auto-generated catch block
											Thread.currentThread().interrupt();
											LOGGER.error("error", e);
										}
									}else{
										LOGGER.error("Abnormal Report {}", reportReq.getCarrierKey());
									}
								}else{
									LOGGER.error("Already processing Report {} {}", resultTableName, reportReq.getCarrierKey());
								}
							}catch (NumberFormatException e){
								LOGGER.error("NumberFormatException timestamp error {}", reportReq.getCarrierKey());
							}
							
						}
					}catch (NumberFormatException e){
						LOGGER.error("NumberFormatException carrier error {}", reportReq.getCarrierKey());
					}
					break;
				default :
					LOGGER.error("Unkown DB Manager status {}", dbQueData.getStatus());
					break;
				}
				
				dbQueData = null;
			}else{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					LOGGER.error("error", e);
					Thread.currentThread().interrupt();
				}
			}
		}
		
		destroy(dbQueData);
	}
	
	public void destroy(TSQueData dbQueData){
		LOGGER.info("TSDBManager Destroy");
		if (dbQueData != null){
			FileQue ioFile = new IOFileQue(dbQue.getQueName());
			try{
				ioFile.open();
				ioFile.enque(mapper.writeValueAsBytes(dbQueData));
				ioFile.close();
			}catch(IOException e){
				LOGGER.error("error", e);
			}
		}
	}
	public String getCarrierType(String carrierKey){
		return carrierKey.split("_")[2];
	}
}
