package com.rcs.service;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rcs.DefaultThreadFactory;
import com.rcs.RCSSendServer;
import com.rcs.exception.TokenException;
import com.rcs.request.MessageRequest;
import com.rcs.request.TokenRequest;
import com.rcs.request.TokenRequest.TokenInfo;

import ib.db.MysqlSessionDAO;
import ib.db.schema.RCSResCode;
import ib.db.schema.TSRouteInfo;
import ib.db.MysqlSessionFactory;
import ib.pdu.mfep.PDUGwMt;
import ib.pdu.mfep.PDUReportReq;
import ib.pdu.mfep.RecipientInfo;
import ib.pdu.util.ByteUtil;
import ib.pdu.util.TSQueData;
import ib.pdu.util.TSQueData.TSQueUtil;
import ib.pdu.util.FileQue;
import ib.pdu.util.IOFileQue;
import ib.pdu.util.NIOFileQue;
import ib.rcs.msg.RCSMessage;
import ib.rcs.msg.RCSResponse;
import ib.rcs.msg.RCSMessage.Button.Suggestions.Action.UrlAction;
import ib.ts.config.Config;
import ib.ts.config.RCSInfo;

public class RCSSendThread implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(RCSSendThread.class);
	
	private ThreadPoolExecutor threadPool;
	private String tsCode;
	private int netId;
	
	private static String queDirName = Config.getQueueDir();		
	
	private static String tsDBUrl = Config.getTSDBUrl();
	private static String tsDBUser = Config.getTSDBUser();
	private static String tsDBPassword = Config.getTSDBPassword();
	
	private int reSendCount;
	private int reSendTime;
	private int timeout;
	
	private long startTime;
	private String carrierType;
	
	private RandomAccessFile seqFile;
	
	private ObjectMapper mapper;
	
	private LinkedBlockingQueue<FileQue> sendQueList = new LinkedBlockingQueue<FileQue>();
	private ConcurrentHashMap<Integer, FileQue> dbQueMap = new ConcurrentHashMap<Integer, FileQue>();
	
	private LinkedBlockingQueue<PDUGwMt> noAckMap = new LinkedBlockingQueue<PDUGwMt>();
	private TokenRequest tokenRequest;
	private String carrierURL;

	private boolean bTokenError = false;
	

    private static RestTemplate restTemplate = new RestTemplate();

    public static void createRestemplate(RCSInfo rcsInfo) {
        HttpComponentsClientHttpRequestFactory crf = new HttpComponentsClientHttpRequestFactory();
        crf.setReadTimeout(rcsInfo.getSendTimeOut()*1000);
        crf.setConnectTimeout(rcsInfo.getSendTimeOut()*1000);

        HttpClient httpClient = HttpClientBuilder.create()
             .setMaxConnTotal(rcsInfo.getThreadMaxPoolSize())
             .setMaxConnPerRoute(50)
             .evictIdleConnections(2000L, TimeUnit.MILLISECONDS)
             .build();
        crf.setHttpClient(httpClient);

        restTemplate.setRequestFactory(crf);
   }
	
	public RCSSendThread(ThreadPoolExecutor threadPool, String tsCode) {
		// TODO Auto-generated constructor tsID
		this.tsCode = tsCode;
		
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		
		RCSInfo rcsInfo = Config.getRcsInfo(tsCode);
		this.netId = rcsInfo.getCarrierNet();
		this.reSendCount = rcsInfo.getReSendCnt();
		this.reSendTime = rcsInfo.getReSendTime();
			
		this.threadPool = threadPool;
		
		LOGGER.info("Resend Policy Count {} Time{}", reSendCount, reSendTime);

		Path seqPath = Paths.get(Config.getSeqDir()+"/"+tsCode+".seq");
		try {
			seqFile = new RandomAccessFile(seqPath.toAbsolutePath().toString(), "rw");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.info("error", e);
			Thread.currentThread().interrupt();
		}
		
		carrierURL = rcsInfo.getCarrierIp()+":"+rcsInfo.getCarrierPort();
		carrierType = rcsInfo.getCarrierType();
		
		createRestemplate(rcsInfo);
		LOGGER.info("Send Timeout {}", timeout);
		LOGGER.info("Carrier url {}", carrierURL);
		LOGGER.info("RCS APi url {}", Config.getRCSApiUrl());
		
		try {
			tokenRequest = new TokenRequest(Config.getRCSApiUrl(), rcsInfo.getCarrierId(), rcsInfo.getCarrierPassword());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.info("error", e);
			Thread.currentThread().interrupt();
		}
		loadConfig();
		LOGGER.info("Re-Send Count {}", reSendCount);
		startTime = System.currentTimeMillis()/1000;
	}
	
	public void loadConfig(){
		Path path = Paths.get(queDirName);
		MysqlSessionFactory mySqlSessionFactory = null;
		try {
			BasicDataSource source = new BasicDataSource();
			source.setDriverClassName("com.mysql.jdbc.Driver");
		    source.setUrl(tsDBUrl);
		    source.setUsername(tsDBUser);
		    source.setPassword(tsDBPassword);
		    
			mySqlSessionFactory = new MysqlSessionFactory(source);
			mySqlSessionFactory.getSqlSessionFactoryBean().setMapperLocations(new PathMatchingResourcePatternResolver().getResources("mapper.xml"));
			MysqlSessionDAO mysqlSessionDAO = mySqlSessionFactory.getSqlSessionTemplate().getMapper(MysqlSessionDAO.class);
			
			Iterator<FileQue> itSendQue = sendQueList.iterator();
			while(itSendQue.hasNext()){
				itSendQue.next().close();
			}
			sendQueList.clear();
			
			List<TSRouteInfo> routeRuleInfos = mysqlSessionDAO.getRtRuleInfo(tsCode);
			String tsidDirName = path.toAbsolutePath()+"/";
			Iterator<TSRouteInfo> itRouteRuleInfo = routeRuleInfos.iterator();
			while(itRouteRuleInfo.hasNext()){
				
				TSRouteInfo routeRuleInfo = itRouteRuleInfo.next();
				FileQue que = new NIOFileQue(tsidDirName+netId+"."+routeRuleInfo.getRtgId()+".que");
				que.setRatio(routeRuleInfo.getRatio());
				try {
					if (que.open()){
						
					}else{
						LOGGER.error("fail, Queue Open {}", que.getQueName());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LOGGER.info("error", e);
					Thread.currentThread().interrupt();
				}
				sendQueList.add(que);
				LOGGER.info("Send Queue Info {}", que.getQueName());
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("error", e);
		}finally{
			try {
				if (mySqlSessionFactory != null)
					mySqlSessionFactory.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.error("SQLException", e);
			}
		}
		
	}
	
	
	public String makeTransactionId(String rsMsgKey) throws IOException{
		String transactionId;
		
		int seq = 0;
		FileLock fileLock = null;
		while(true){
			try{
				fileLock = seqFile.getChannel().tryLock();
				if(fileLock!=null){
					break;
				}
			}catch(OverlappingFileLockException e){
				continue;
			}
		}
		
		synchronized (seqFile) {
			try{
				seqFile.seek(0);
				try{
					seq = seqFile.readInt();
				}catch(EOFException e){
					
				}
				
				seq++;
				seq %= 10000;
				
				seqFile.seek(0);
				seqFile.writeInt(seq);
				seqFile.getFD().sync();
			}catch (IOException e){
				LOGGER.error("error", e);
				if (fileLock != null) fileLock.release();
			}finally{
			
			}
			String timestamp = String.valueOf(System.currentTimeMillis()/1000);
			int dbQuekey = getDBQueKey(rsMsgKey);
			transactionId = netId+"_"+tsCode+"_"+carrierType+"_"+dbQuekey+"_"+timestamp+"_"+String.format("%04d", seq);
		}
		
		if (fileLock != null)
			fileLock.release();
		
		return transactionId;
	}
	
	public void run() {
		Iterator<FileQue> itSendQue = sendQueList.iterator();
		TokenInfo tokenInfo = new TokenInfo();
		try {
			int responseCode = tokenRequest.requestToken();
			if (responseCode == 200 || responseCode == 202){
				RCSResponse response = mapper.readValue(tokenRequest.getResponse(), RCSResponse.class);
				tokenInfo.setRequestTime(System.currentTimeMillis()/1000);
				tokenInfo.setTokenType(response.getTokenType());
				tokenInfo.setToken(response.getAccessToken());
				tokenInfo.setExpired(response.getExpires());
				LOGGER.info("Token {} TokenType {} Expired_in {}", tokenInfo.getToken(), tokenInfo.getTokenType(), tokenInfo.getExpired());
			}else{
				LOGGER.error("Token Error {}", responseCode);
				//destroy();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("error", e);
			//destroy();
		}
		
		int iEmptyQueCnt = 0;
		
		while(true){
			if (Thread.currentThread().isInterrupted()){
				break;
			}
			
			if (System.currentTimeMillis()/1000 - startTime >= Config.getReloadConfigTime()){
				loadConfig();
				itSendQue = sendQueList.iterator();
				startTime = System.currentTimeMillis()/1000;
			}
		
			long lNow = System.currentTimeMillis()/1000;
			
			if(lNow > tokenInfo.getRequestTime() + tokenInfo.getExpired() || bTokenError){
				LOGGER.info("Token Expired {} {} {}", lNow, tokenInfo.getRequestTime() + tokenInfo.getExpired(), bTokenError);
				try {
					int responseCode = tokenRequest.requestToken();
					if (responseCode == 200 || responseCode == 202){
						RCSResponse response = mapper.readValue(tokenRequest.getResponse(), RCSResponse.class);
						tokenInfo.setRequestTime(System.currentTimeMillis()/1000);
						tokenInfo.setTokenType(response.getTokenType());
						tokenInfo.setToken(response.getAccessToken());
						tokenInfo.setExpired(response.getExpires());
						LOGGER.info("Token {} TokenType {} Expired_in {}", tokenInfo.getToken(), tokenInfo.getTokenType(), tokenInfo.getExpired());
					}else{
						Thread.currentThread().interrupt();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Thread.currentThread().interrupt();
					break;
				}
			}
			
			boolean bTooFast = false;
			byte[] buffer = null;
			PDUGwMt pduReq = null;
			FileQue sendQue = null; 
			TSQueData data= null;
			
			if (noAckMap.size()>0){
				Iterator<PDUGwMt> itNoAckMap = noAckMap.iterator();
				while(itNoAckMap.hasNext()){
					try{
						pduReq = itNoAckMap.next();
					}catch (Exception e){
						LOGGER.info("error", e);
					}
					
					if (System.currentTimeMillis()/1000 - pduReq.getTsSendTime() > reSendTime){
						if (pduReq.getReSend() >= reSendCount ){
							noAckMap.remove(pduReq);
							LOGGER.info("re-Send over {} {}", pduReq.getReSend(), pduReq.toString());
							try {
								doNakProcess(pduReq, "YIBErr");
							} catch (JsonProcessingException e) {
								// TODO Auto-generated catch block
								LOGGER.error("error", e);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								LOGGER.error("error", e);
							} catch (Exception e){
								LOGGER.error("error", e);
							}
							break;
						}else{
							LOGGER.info("noAck Map {} {}", pduReq.getReSend(), pduReq.toString());
							bTooFast = true;
							pduReq.setReSend(pduReq.getReSend()+1);
							break;
						}
					}else{
						
					}
				}
			}
			
			if (bTooFast == false){
				if(itSendQue.hasNext()){
					sendQue = itSendQue.next();
				}else{
					itSendQue = sendQueList.iterator();
					continue;
				}
				
				if (sendQue.getRatio() > sendQue.getCnt()){
					try {
						if ((buffer = sendQue.deque()) == null){
							iEmptyQueCnt++;
							//큐 전체가 비어있음
							if(iEmptyQueCnt == sendQueList.size()){
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									LOGGER.error("error", e);
									Thread.currentThread().interrupt();
								}
								
								iEmptyQueCnt =0;
							}
							continue;
						}else{
							sendQue.setCnt(sendQue.getCnt()+1);
							iEmptyQueCnt=0;
							
							data = mapper.readValue(buffer, TSQueData.class);
							pduReq = data.getPduGwMt();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						LOGGER.error("error",e);
						continue;
					}
				}else{
					iEmptyQueCnt =0;
					sendQue.setCnt(0);
					continue;
				}
			}
		
			LOGGER.info("deque {}", pduReq.toString());
			pduReq.setTsSendTime(System.currentTimeMillis()/1000);
			String transactionId="";
			try {
				if (pduReq.getCarrierKey().equals(pduReq.getRsMsgKey())){
					transactionId = makeTransactionId(pduReq.getRsMsgKey());
				}else{
					transactionId = pduReq.getCarrierKey();
				}
				
				pduReq.setCarrierKey(transactionId);
				
				if (!bTooFast){
					noAckMap.add(pduReq);
					doDBQueEnque(doSendQue(pduReq));
				}
				
				
			} catch (IOException  e) {
				// TODO Auto-generated catch block
				LOGGER.info("error", e);
			}
			

			while(true){
				try{
					threadPool.execute(new SendHandler(pduReq, tokenInfo));
					break;
				}catch (RejectedExecutionException e) {
					LOGGER.warn("Task Pool is Full..");
					try {
						Thread.sleep(500);
						continue;
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						LOGGER.error("error", e1);
						Thread.currentThread().interrupt();
						break;
					}
				}
			}
			
		}
		//destroy();
	}
	
	public boolean checkCapability(String recipient){
		/*
		try {
			CapabilityRequest capabilityRequest = new CapabilityRequest(carrierURL, token);
			int responseCode = capabilityRequest.requestCapability(recipient);
			if (responseCode == 200 || responseCode == 202){
				RCSResponse response = mapper.readValue(capabilityRequest.getResponse(), RCSResponse.class);
				if (response.getData().get("reachability").equals("false")){
					return false;
				}
			}else{
				LOGGER.error("fail ResponseCode {}", responseCode);
				return false;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			LOGGER.error("fail checkCapability recipientNUM {}", recipient);
			return false;
		}
		*/
		return true;
	}
	
	public String makeRCSMessage(PDUGwMt req) throws Exception{
		JsonNode jsonNode = mapper.readTree(req.getContent());
		((ObjectNode)jsonNode).put("agcMsgId", req.getCarrierKey());
		String recipient = req.getRecipients().get(0).getConuntryCode() +req.getRecipients().get(0).getRecipientNum();
		((ObjectNode)jsonNode).put("contactNum", recipient);
		((ObjectNode)jsonNode).put("resellerId", "infobank_r");
		return jsonNode.toString();
	}	
	public TSQueData doSendQue(PDUGwMt pduReq){
		return new TSQueData(TSQueUtil.TS_RECV_MT, new PDUReportReq(pduReq));
		
	}
	public void doAckProcess(PDUGwMt pduReq) throws JsonProcessingException, IOException{
		noAckMap.remove(pduReq);
		doDBQueEnque(new TSQueData(TSQueUtil.TS_SEND_ACK, new PDUReportReq(pduReq)));
	}
	public void doNakProcess(PDUGwMt pduReq, String ibRsltCode, String netCode) throws JsonProcessingException, IOException{
		noAckMap.remove(pduReq);
		doDBQueEnque(enqueDBQueNak(pduReq, ibRsltCode, netCode));
	}
	public void doNakProcess(PDUGwMt pduReq, String netCode) throws JsonProcessingException, IOException{
		noAckMap.remove(pduReq);
		doDBQueEnque(enqueDBQueNak(pduReq, "", netCode));
	}
	public TSQueData enqueDBQueNak(PDUGwMt pduReq, String ibRsltCode, String netCode){
		PDUReportReq reportReq = new PDUReportReq(pduReq);		
		reportReq.setTsRsltSendTime(System.currentTimeMillis()/1000);
		reportReq.setTsRsltRecvTime(System.currentTimeMillis()/1000);
		reportReq.setTsRsltTime(System.currentTimeMillis()/1000);
		if(!ibRsltCode.equals(""))
			reportReq.setIbRsltCode(ibRsltCode);
		reportReq.setNetCode(netCode);
		
		return new TSQueData(TSQueUtil.TS_SEND_NAK, reportReq);
	}

	public void destroyEnqueNoAckMap(){
		LOGGER.info("destroyEnqueNoAckMap");
	
		Iterator<PDUGwMt> itNoAckMap = noAckMap.iterator();
		while(itNoAckMap.hasNext()){			
			PDUGwMt pduGwMt = itNoAckMap.next();
			//pduGwMt.setCarrierKey(pduGwMt.getRsMsgKey());	

			TSQueData tsQueData = new TSQueData(TSQueUtil.TS_RECV_RS, pduGwMt);
			String queName = Config.getQueueDir()+"/"+pduGwMt.getCarrier()+"."+pduGwMt.getRouteGroup()+".que";
			LOGGER.info("{}", queName);
			FileQue ioFile = new IOFileQue(queName);
			try {
				ioFile.open();
				ioFile.enque(mapper.writeValueAsBytes(tsQueData));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOGGER.error("error", e);
			}finally{
				try {
					ioFile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LOGGER.error("error", e);
				}
			}
				
			LOGGER.info("Remain noAckMap Enque SendQue {}", pduGwMt.toString());
		}
	}
	public int getDBQueKey(String rsMsgKey){
		return (int)(Long.parseLong(rsMsgKey) % Config.getDBQueCnt())+1;
	}
	public TSQueData doDBQueEnque(TSQueData tsQueData) throws JsonProcessingException, IOException{
		PDUGwMt pduReq = tsQueData.getPduGwMt();
		
		int key = getDBQueKey(pduReq.getRsMsgKey());
		Path path = Paths.get(queDirName);
		String dbQueName = path.toAbsolutePath()+"/db.que."+key;
		
		if (!dbQueMap.containsKey(key)){
			LOGGER.debug("DB Queue Info {}", dbQueName);
			FileQue dbQue = new NIOFileQue(dbQueName);
			if (dbQue.open()){
				
			}else{
				LOGGER.error("fail, Queue Open {}", dbQue.getQueName());
			}
			dbQueMap.put(key, dbQue);
		}
		
		try{
			if (dbQueMap.get(key).enque(mapper.writeValueAsBytes(tsQueData))){
				LOGGER.debug("DBQue enque {}", pduReq.toString());
			}
		}catch(ClosedChannelException e){
			LOGGER.error("error", e);
			FileQue dbQue = new IOFileQue(dbQueName);
			dbQue.open();
			dbQue.enque(mapper.writeValueAsBytes(tsQueData));
			dbQue.close();
		}
		return tsQueData;
	}
	public void destroy(){
		LOGGER.info("destroy Service");
		
		destroyEnqueNoAckMap();
		Iterator<FileQue> itSendQue = sendQueList.iterator();
		while(itSendQue.hasNext()){
			try {
				itSendQue.next().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOGGER.info("destroy Service");
			}
		}
		/*
		try {
			for (int key : dbQueMap.keySet()){
				dbQueMap.get(key).close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("error", e);
		}
		*/
	}
	class SendHandler implements Runnable{
		private PDUGwMt pduReq;
		private TokenInfo tokenInfo;
		
		public SendHandler(PDUGwMt pduReq, TokenInfo tokeInfo) {
			// TODO Auto-generated constructor stub
			this.pduReq = pduReq;
			this.tokenInfo = tokeInfo;
		}
		public void run() {
			// TODO Auto-generated method stub
			try {
				if(pduReq.getTtl() <= System.currentTimeMillis()/1000){
					LOGGER.info("TTL Over Timeout carrierKey:{}, rsMsgKey:{}, clientMsgKey:{}, ttl:{}",
							pduReq.getCarrierKey(), pduReq.getRsMsgKey(), pduReq.getClientMsgKey(), pduReq.getTtl());
					doNakProcess(pduReq, "2000", "");
				}else{
					Iterator<RecipientInfo> itRecipient = pduReq.getRecipients().iterator();
					while (itRecipient.hasNext()){					
						String recipientNum = itRecipient.next().getRecipientNum();
						if (checkCapability(recipientNum)){
							MessageRequest messageRequest = new MessageRequest(restTemplate, carrierURL, tokenInfo);
							String rcsMessage = makeRCSMessage(pduReq);
							
							pduReq.setTsSendTime(System.currentTimeMillis()/1000);
							int responseCode = messageRequest.sendMessage(rcsMessage, pduReq);
							
							if (responseCode == 200 || responseCode == 202)
							{
								doAckProcess(pduReq);			
							}else if (responseCode == 400){
								if (messageRequest.getResponse().length() > 0 ){
									RCSResponse response = mapper.readValue(messageRequest.getResponse(), RCSResponse.class);
									String netCode = response.getError().get("code");
									if (netCode.equals("4043")){
										throw new TokenException(response.getError().get("code"), response.getError().get("reason"));
									}else{
										doNakProcess(pduReq, netCode);
									}
								}else{
									doNakProcess(pduReq, String.valueOf(responseCode));
								}
							}else if (responseCode == 429){
								pduReq.setReSend(pduReq.getReSend()-1 < 0 ? 0 : pduReq.getReSend()-1);
							}else{
								throw new ConnectTimeoutException();
							}
						}else{
							/*
							 * TODO : Capability 정상 동작 확인 후 NetCode 생성해서 수정 
							 */
						}
					}
				}				
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				LOGGER.error("JsonProcessingException carrierKey:{}, rsMsgKey:{}, clientMsgKey:{} {}",
						pduReq.getCarrierKey(), pduReq.getRsMsgKey(), pduReq.getClientMsgKey(), e);
				try {
					doNakProcess(pduReq, "YIBErr");
				} catch (JsonProcessingException e1) {
					// TODO Auto-generated catch block
					LOGGER.error("JsonProcessingException", e1);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					LOGGER.error("IOException", e1);
				}
			} catch (HttpHostConnectException |ConnectTimeoutException | SocketTimeoutException e){
				LOGGER.error("error carrierKey:{}, rsMsgKey:{}, clientMsgKey:{}, error:{}",
						pduReq.getCarrierKey(), pduReq.getRsMsgKey(), pduReq.getClientMsgKey(), e);
				pduReq.setReSend(pduReq.getReSend()-1 < 0 ? 0 : pduReq.getReSend()-1);
			}catch (TokenException e){
				LOGGER.error("error", e);
				pduReq.setReSend(pduReq.getReSend()-1 < 0 ? 0 : pduReq.getReSend()-1);
				bTokenError = true;
			}catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.error("Exception carrierKey:{}, rsMsgKey:{}, clientMsgKey:{}, error:{}",
						pduReq.getCarrierKey(), pduReq.getRsMsgKey(), pduReq.getClientMsgKey(), e);
			} 
		}
	}
}