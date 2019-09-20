package com.ts.service;

import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import ib.db.MysqlErrorHandler;
import ib.db.MysqlSessionDAO;
import ib.pdu.mfep.PDUGwMt;
import ib.pdu.util.FileQue;
import ib.pdu.util.IOFileQue;
import ib.pdu.util.TSQueData;
import ib.pdu.util.TSQueData.TSQueUtil;

public class DBManagerHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(DBManagerHandler.class);
	private String resultTableName;
	private ObjectMapper mapper;
	private Map<String, FileQue> reportQueMap;
	private MysqlSessionDAO mysqlSessionDAO =null;
	
	public DBManagerHandler(String resultTableName, Map<String, FileQue> reportQueMap, MysqlSessionDAO mysqlSessionDAO) {
		// TODO Auto-generated constructor stub
		this.resultTableName = resultTableName;
		this.reportQueMap = reportQueMap;
		this.mysqlSessionDAO = mysqlSessionDAO;
		
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
	public boolean doResult(PDUGwMt reportReq){
		boolean ret=true;
		if((ret = doResultMapMoveToLog(reportReq)) == true){
			LOGGER.debug("move ResultMap {}", reportReq.toString());
			if((ret = reportQueEnque(reportReq)) == true){
				LOGGER.info("enque Result que {}", reportReq.toString());
				if((ret = doResultMapDelete(reportReq.getTsCode(),
											reportReq.getCarrierKey(),
										    reportReq.getRecipients().get(0).getRecipientOrder(),
										    reportReq.getRecipients().get(0).getRecipientNum()))){
					LOGGER.debug("delete ResultMap {}", reportReq.toString());
				}else{
					LOGGER.error("fail, delete {} {}", resultTableName, reportReq.toString());
				}
			}else{
				LOGGER.error("fail, report enque {} {}", resultTableName, reportReq.toString());
			}
		}else{
				LOGGER.error("fail, move To log table {} {}", resultTableName, reportReq.toString());
		}
		return ret;
	}
	
	public boolean reportQueEnque(PDUGwMt reportReq){
		FileQue reportQue = reportQueMap.get(reportReq.getRsID());
		TSQueData tsQueData = new TSQueData(TSQueUtil.TS_RECV_REPORT, reportReq);
		try {
			reportQue.enque(mapper.writeValueAsBytes(tsQueData));
		}catch (ClosedChannelException e){
			try{
				FileQue ioFile = new IOFileQue(reportQue.getQueName());
				ioFile.open();
				ioFile.enque(mapper.writeValueAsBytes(tsQueData));
				ioFile.close();
			}catch(IOException e1){
				LOGGER.error("error", e1);
				return false;
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("error", e);
			return false;
		}
		return true;
	}
	
	public boolean doResultMapMoveToLog(PDUGwMt resultMap){
		String logtableName = getLogTableName(resultMap.getRsRecvTime());
		MysqlErrorHandler mysqlErrorHandler = doResultMapUpdate(logtableName,
																resultMap.getCarrierKey(),
																resultMap.getMsgStatus(),
																resultMap.getNetCode(),
															 	resultMap.getIbRsltCode(),
																resultMap.getTsRsltTime(),
																resultMap.getTsCode(),
																resultMap.getRsMsgKey(),
																resultMap.getClientMsgKey(),
																resultMap.getRecipients().get(0).getRecipientOrder(),
																resultMap.getRecipients().get(0).getRecipientNum());
		if (!mysqlErrorHandler.getCode().equals("00000") && !mysqlErrorHandler.getCode().equals("23000")){
			LOGGER.error("fail, move ReusltMap {} {} {} {}", logtableName, mysqlErrorHandler.getCode(), mysqlErrorHandler.getMsg());
			return false;
		}
		return true;
	}
	public String getLogTableName(long lRsRecvTime){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
		return resultTableName+"_"+simpleDateFormat.format(new Date(lRsRecvTime*1000));
	}
	public boolean doResultMapDelete(int iTsCode, String strNetKey, int iRecipientSeq,  String strRecipientNum){
		MysqlErrorHandler mysqlErrorHandler = mysqlSessionDAO.deleteResultMap(resultTableName, iTsCode, strNetKey, iRecipientSeq,  strRecipientNum);
		if (!mysqlErrorHandler.getCode().equals("00000")){
			LOGGER.error("delete fail ReusltMap {} {} {} {}", resultTableName, mysqlErrorHandler.getCode(), mysqlErrorHandler.getMsg());
			return false;
		}
		return true;
	}
	
	public MysqlErrorHandler doResultMapUpdate(String logtableName,
											   String strNetKey,
											   int iMsgStatus,
											   String strNetCode,
											   String strIbRslt,
											   long lTsRsltDate,
											   int iTsCode,
											   String strRsKey,
											   String strEmmaKey,
											   int iRecipientSeq,
											   String strRecipientNum){
		MysqlErrorHandler mysqlErrorHandler = mysqlSessionDAO.updateMoveLogResultMap(resultTableName,
																					 logtableName,
																					 strNetKey,
																					 iMsgStatus,
																					 strNetCode,
																					 strIbRslt,
																					 lTsRsltDate,
																					 iTsCode,
																					 strRsKey,
																					 strEmmaKey,
																					 iRecipientSeq,
																					 strRecipientNum);
		return mysqlErrorHandler;
	}
	public void createLogTable(long time){
		mysqlSessionDAO.createResultMapTable(getLogTableName(time));
	}
}
