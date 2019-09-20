package ib.ts.config;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
	private static Logger LOGGER = LoggerFactory.getLogger(Config.class);
	
	private static String propertyFile;
	private static Map<String, RCSInfo> rcsInfos = new HashMap<String, RCSInfo>();
	
	private static String TS_RCS_LISTEN_PORT = "ts.rcs.listen.port";
	private static String TS_RSLT_LISTEN_PORT = "ts.rslt.listen.port";
	
	private static String TS_RELOAD_CONFIG_TIME = "ts.config.reload.time";
	
	private static String QUEUE_DIR = "ts.que.dir";
	private static String CONF_DIR ="ts.conf.dir";
	private static String SEQ_DIR = "ts.seq.dir";
	
	private static String TS_RECV_WORKER_THREAD_CNT = "ts.recv.workthread.cnt";
	private static String TS_DBQUE_CNT = "ts.dbque.cnt";
	
	private static String RCS_API_URL = "rcs.api.url";

	private static String TS_DB_URL = "rcs.db.url";
	private static String TS_DB_USER = "rcs.db.user";
	private static String TS_DB_PASSWORD = "rcs.db.password";
	
	private static String TS_PROC_RECIVER="ts.proc.receiver";
	private static String TS_PROC_RCS_SENDER="ts.proc.rcssender";
	private static String TS_PROC_DBMANAGER="ts.proc.dbman";
	private static String TS_PROC_RSLT_SENDER="ts.proc.rsltsender";
	
	private static Configuration config;
	
	static {
		propertyFile = System.getProperty("conf");
		try {
			
			LOGGER.info("load service property from {}", propertyFile);
			config = new PropertiesConfiguration(System.getProperty("conf"));
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			LOGGER.error("cannt not find property {}", propertyFile);
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static int getRCSListenPort(){
		return config.getInt(TS_RCS_LISTEN_PORT);
	}
	public static int getRsltListenPort(){
		return config.getInt(TS_RSLT_LISTEN_PORT);
	}
	public static int getReloadConfigTime(){
		if (!config.containsKey(TS_RELOAD_CONFIG_TIME)){
			return 86400;
		}
		if (config.getInt(TS_RELOAD_CONFIG_TIME)==0){
			return 86400;
		}
		return config.getInt(TS_RELOAD_CONFIG_TIME);
	}
	public static String getConfDir(){
		return config.getString(CONF_DIR);
	}
	public static String getQueueDir(){
		return config.getString(QUEUE_DIR);
	}
	public static String getSeqDir(){
		return config.getString(SEQ_DIR);
	}
	public static int getRecvWorkerThreadCnt(){
		return config.getInt(TS_RECV_WORKER_THREAD_CNT);
	}
	public static int getDBQueCnt(){
		if(!config.containsKey(TS_DBQUE_CNT)){
			return 1;
		}
		return config.getInt(TS_DBQUE_CNT);
	}
	public static Map<String, RCSInfo> getRcsInfos(){
		String opt;
		int tsCodeCnt = config.getInt("ts.tscode.cnt", 0);
		for (int i=0; i<tsCodeCnt; i++){
			String tsCode = config.getString(String.format("ts.rcscode.%02d", i+1));
			RCSInfo rcsInfo = new RCSInfo();
			rcsInfo.setCarrierType(config.getString(tsCode+".carrier.type"));
			rcsInfo.setCarrierNet(config.getInt(tsCode+".carrier.net"));
			rcsInfo.setCarrierIp(config.getString(tsCode+".carrier.ip"));
			rcsInfo.setCarrierPort(config.getInt(tsCode+".carrier.port"));
			rcsInfo.setCarrierId(config.getString(tsCode+".carrier.id"));
			rcsInfo.setCarrierPassword(config.getString(tsCode+".carrier.password"));
			
			opt = tsCode+".resend.cnt";
			if (config.containsKey(opt))
				rcsInfo.setReSendCnt(config.getInt(opt));
			
			opt = tsCode+".resend.time";
			if (config.containsKey(opt))
				rcsInfo.setReSendTime(config.getInt(opt));
			
			opt = tsCode+".send.timeout";
			if (config.containsKey(opt))
				rcsInfo.setSendTimeOut(config.getInt(opt));
			
			opt = tsCode+".threadpool.coresize";
			if (config.containsKey(opt))
				rcsInfo.setThreadPoolCoreSize(config.getInt(opt));
			
			opt = tsCode+".threadpool.maxsize";
			if (config.containsKey(opt))
				rcsInfo.setThreadMaxPoolSize(config.getInt(opt));
			
			opt = tsCode+".threadpool.keepalivetime";
			if (config.containsKey(opt))
				rcsInfo.setThreadKeepAliveTime(config.getInt(opt));
			
			rcsInfos.put(tsCode, rcsInfo);
		}
		return rcsInfos;
	}
	
	
	public static RCSInfo getRcsInfo(String tsCode){
		RCSInfo rcsInfo;
		if(rcsInfos.containsKey(tsCode)){
			rcsInfo = rcsInfos.get(tsCode);
		}
		else{
			rcsInfo = new RCSInfo();
			String opt;
			LOGGER.info("test {}", config.getString(tsCode+".carrier.type"));
			rcsInfo.setCarrierType(config.getString(tsCode+".carrier.type"));
			rcsInfo.setCarrierNet(config.getInt(tsCode+".carrier.net"));
			rcsInfo.setCarrierIp(config.getString(tsCode+".carrier.ip"));
			rcsInfo.setCarrierPort(config.getInt(tsCode+".carrier.port"));
			rcsInfo.setCarrierId(config.getString(tsCode+".carrier.id"));
			rcsInfo.setCarrierPassword(config.getString(tsCode+".carrier.password"));
			
			opt = tsCode+".resend.cnt";
			if (config.containsKey(opt))
				rcsInfo.setReSendCnt(config.getInt(opt));
			
			opt = tsCode+".resend.time";
			if (config.containsKey(opt))
				rcsInfo.setReSendTime(config.getInt(opt));
			
			opt = tsCode+".send.timeout";
			if (config.containsKey(opt))
				rcsInfo.setSendTimeOut(config.getInt(opt));
			
			opt = tsCode+".threadpool.coresize";
			if (config.containsKey(opt))
				rcsInfo.setThreadPoolCoreSize(config.getInt(opt));
			
			opt = tsCode+".threadpool.maxsize";
			if (config.containsKey(opt))
				rcsInfo.setThreadMaxPoolSize(config.getInt(opt));
			
			opt = tsCode+".threadpool.keepalivetime";
			if (config.containsKey(opt))
				rcsInfo.setThreadKeepAliveTime(config.getInt(opt));
		}
		return rcsInfo;
	}
	
	public static String getRCSApiUrl(){
		return config.getString(RCS_API_URL);
	}

	public static String getTSDBUrl(){
		return config.getString(TS_DB_URL);
	}
	public static String getTSDBUser(){
		return config.getString(TS_DB_USER);
	}
	public static String getTSDBPassword(){
		return config.getString(TS_DB_PASSWORD);
	}
	
	/*Process Name*/
	public static String getReceiverName(){
		return config.getString(TS_PROC_RECIVER);
	}
	public static String getRCSSenderName(){
		return config.getString(TS_PROC_RCS_SENDER);
	}
	public static String getDBManagerName(){
		return config.getString(TS_PROC_DBMANAGER);
	}
	public static String getRsltSenderName(){
		return config.getString(TS_PROC_RSLT_SENDER);
	}
	
	public static void reloadConfigFile(){
		try {
			LOGGER.info("load service property from {}", propertyFile);
			config = new PropertiesConfiguration(System.getProperty("conf"));
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			LOGGER.error("cannt not find property {}", propertyFile);
			e.printStackTrace();
			System.exit(1);
		}
	}
}
