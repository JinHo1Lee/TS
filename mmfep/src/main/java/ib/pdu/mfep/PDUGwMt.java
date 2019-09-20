package ib.pdu.mfep;

import java.nio.ByteBuffer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import ib.pdu.mfep.exception.PduException;

public interface PDUGwMt {
	public byte[] encode()  throws PduException;
	public String getRouteGroup();

	public int getResCode();
	public void setResCode(int resCode);
	public int getContentType();

	public void setContentType(int contentType);
	public void setNumPorted(String numPorted);
	public int getWordCnt();
	public void setWordCnt(int wordCnt);
	public long getTtl();
	public void setTtl(long ttl);
	public String getNumPorted();
	public String getBridgeTag();
	public void setBridgeTag(String bridgeTag);
	public String getEmmaTag();
	public void setEmmaTag(String emmaTag);
	public String getPriority();
	public void setPriority(String priority);
	public String getCarrierKey();
	public void setCarrierKey(String carrierKey);
	public int getReSend();
	public void setReSend(int reSend);
	public long getTsRecvTime();
	public void setTsRecvTime(long tsRecvTime);
	public long getTsSendTime();
	public void setTsSendTime(long tsSendTime);
	public String getNetCode();
	public void setNetCode(String netCode);
	public int getMsgClass();
	public void setMsgClass(int msgClass) ;
	public AttachFileKeys getAttachFileKeys();
	public void setAttachFileKeys(AttachFileKeys attachFileKeys);
	

	public int getPduType();
	public void setPduType(int pduType);
	public String getClientMsgKey();
	public void setClientMsgKey(String clientMsgKey);
	public String getContent();
	public void setContent(String content);
	public int getMsgType();
	public void setMsgType(int msgType);
	public String getCallBack() ;
	public void setCallBack(String callBack);
	public String getRsMsgKey();
	public void setRsMsgKey(String rsMsgKey);
	public String getRsID();
	public void setRsID(String rsID);
	public String getClientCode();
	public void setClientCode(String clientCode);
	public int getTsCode() ;
	public void setTsCode(int tsCode);
	public void setRouteGroup(String routeGroup);
	public Recipients getRecipients();
	public void setRecipients(Recipients recipients);
	public String getSubject();
	public void setSubject(String subject);
	
	public long getTsRsltRecvTime();
	public void setTsRsltRecvTime(long tsRlstRecvTime);
	public long getTsRsltSendTime();
	public void setTsRsltSendTime(long tsRsltSendTime);
	public long getTsRsltTime();
	public void setTsRsltTime(long tsRsltTime);
	
	public String getIbRsltCode();
	public void setIbRsltCode(String ibRsltCode);
	public int getCarrier();
	public void setCarrier(int carrier);
	public int getMsgStatus();
	public void setMsgStatus(int msgStatus);
	public String getStrFileKeyList();
	public void setStrFileKeyList(String strFileKeyList);
	public String getStrFileKeyCarrier();
	public void setStrFileKeyCarrier(String strFileKeyCarrier);
	public int getReportType();
	public void setReportType(int reportType);
	public long getRsRecvTime();
	public void setRsRecvTime(long rsRecvTime);
}
