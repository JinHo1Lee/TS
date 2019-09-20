package com.ts.handler;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ib.db.MysqlSessionDAO;
import ib.db.MysqlSessionFactory;
import ib.db.schema.TSRsInfo;
import ib.pdu.mfep.PDUGwMtResCode;
import ib.pdu.mfep.PDUReportReq;
import ib.pdu.util.ByteUtil;
import ib.pdu.util.FileQue;
import ib.pdu.util.IOFileQue;
import ib.pdu.util.NIOFileQue;
import ib.pdu.util.TSQueData;
import ib.pdu.util.TSQueData.TSQueUtil;
import ib.rcs.msg.RCSResponse;
import ib.ts.config.Config;

public class ReportHandler implements Runnable{
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportHandler.class);
	
	private static String queDirName = Config.getQueueDir();
	private ObjectMapper mapper;
	private ConcurrentHashMap<String, FileQue> recvQueList = new ConcurrentHashMap<String, FileQue>();
	
	private FileQue resultQue;
	
	public ReportHandler(String apiIp) {
		// TODO Auto-generated constructor stub
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		
		MDC.put("discriminator", String.format("rsltrecv_%s", apiIp));
		try {
			String queName = queDirName +"/" + apiIp+".rsltrecv.que";
			LOGGER.info("reportQueName {} ", queName);
			resultQue = new NIOFileQue(queName);
			resultQue.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("error", e);
		}
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			if (Thread.currentThread().isInterrupted()) {
				break;
			}
			
			byte[] buffer = null;
			try{
				if((buffer = resultQue.deque()) != null){
					int datalen = ByteUtil.getint(buffer);
					String jsonData = new String (ByteUtil.getBytes(buffer, 4, datalen)).trim();
					LOGGER.debug("{}", jsonData);
					List<RCSResponse> rcsResponses = mapper.readValue(jsonData, new TypeReference<List<RCSResponse>>() {});
					for(RCSResponse rcsResponse : rcsResponses){
						try {
							TSQueData tsQueData = new TSQueData();
							tsQueData.setStatus(TSQueUtil.TS_RECV_REPORT);
							
							PDUReportReq pduReportReq = new PDUReportReq();
							pduReportReq.setCarrierKey(rcsResponse.getId());
							pduReportReq.setNetCode(rcsResponse.getStatus());
							
							String carrierKey = pduReportReq.getCarrierKey();
							String carrierKeyArry[] = carrierKey.split("_");
							
							int dbQueNum = Integer.parseInt(carrierKeyArry[3]);
							if (dbQueNum <=0 || dbQueNum > Config.getDBQueCnt()){
								LOGGER.error("carrier Key Error {}", jsonData);
								continue;
							}
							String dbQueName = "db.que."+dbQueNum;
							FileQue dbQue = null;
							
							if (!recvQueList.containsKey(dbQueName)){
								dbQue = new NIOFileQue(Config.getQueueDir() + "/" + dbQueName);
								dbQue.open();
								recvQueList.put(dbQueName, dbQue);
							}else{
								 dbQue = recvQueList.get(dbQueName);
							}
							tsQueData.setPduReportReq(pduReportReq);
							try{
								dbQue.enque(mapper.writeValueAsBytes(tsQueData));
							}catch (ClosedChannelException e){
								LOGGER.error("error", e);
								FileQue ioFile = new IOFileQue(Config.getQueueDir() +"/" + dbQueName);
								ioFile.open();
								ioFile.enque(mapper.writeValueAsBytes(tsQueData));
								ioFile.close();
							}
							LOGGER.info("(enque) {} {} {}", dbQue.getQueName(), pduReportReq.getCarrierKey(), pduReportReq.getNetCode());
						} catch (IOException e){
							LOGGER.error("error", e);
						} catch (NumberFormatException e){
							LOGGER.error("carrier Key Error {}", jsonData);
						} catch (Exception e){
							LOGGER.error("error {}", jsonData);
						}
					}
				}else{
					Thread.sleep(1000);
				}
			}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LOGGER.error("error", e);
				destroy(buffer);
				Thread.currentThread().interrupt();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOGGER.error("error", e);
				destroy(buffer);
				Thread.currentThread().interrupt();
			} catch (Exception e){
				LOGGER.error("error", e);
			}
		}
	}

	public void destroy(byte[] buffer){
		LOGGER.info("ReportHandler destroy");
		if (buffer != null){
			try {
				FileQue ioFile = new IOFileQue(resultQue.getQueName());
				ioFile.open();
				ioFile.enque(buffer);
				ioFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOGGER.error("error", e);
			}
		}
	}
}
