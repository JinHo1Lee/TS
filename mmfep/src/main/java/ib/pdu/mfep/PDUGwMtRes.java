package ib.pdu.mfep;

import ib.pdu.mfep.exception.PduException;
import ib.pdu.util.ByteUtil;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PDUGwMtRes implements Serializable {

	@Override
	public String toString() {		
		String outStr = "";
		for (int i=0; i<recipients.size(); i++){
			RecipientInfo recipientInfo = recipients.get(i);
			String recpientFormat = recipientInfo.getRecipientOrder()+"_"+recipientInfo.getRecipientNum()+"_"+recipientInfo.getConuntryCode();
			outStr += String.format("tsCode:%s, routeGroup:%s, clientCode:%s, msgType:%s, clientMsgKey:%s, rsMsgKey:%s, rsID:%s, priority:%s, CallBack:%s, Recpient:%s, net:%d, npsend:%s, ttl:%s",
									 tsCode, routeGroup, clientCode, msgType, clientMsgKey, rsMsgKey, rsID, priority, callBack, recpientFormat, recipients.get(i).getRecipientNet(), recipients.get(0).getRecipientNpSend(), ttl);
			if (i>0)outStr +="\n";
		}
		return outStr;
	}

	private static final long serialVersionUID = -7182142576691890290L;

	
	/*RS Data*/
	private int pduType;
	private int msgClass;
	private String clientMsgKey;
	private int contentType;
	private String content;
	private int msgType;
	private String callBack;
	private Recipients recipients;
	private String rsMsgKey;
	private String rsID;
	private String clientCode;
	private String routeGroup;
	private int wordCnt;
	private long ttl;
	private String priority;
	private String numPorted;
	private String bridgeTag;
	private String emmaTag;
	private int tsCode;
	private int resCode;
	
	private AttachFileKeys attachFileKeys;
	

	
	public byte[] encode() throws PduException {

		checkValidation();

		int lengthTemp = 0;
		ByteBuffer buffer = ByteBuffer.allocate(ByteUtil.MAX_PDU_BYTE_LEN);

		/* Header */
		// pduType
		buffer.putInt(pduType);
		
		buffer.mark(); // mark and write bodyLength temporally
		buffer.putInt(0);
		
		buffer.putInt(resCode);
		
		// clientMsgKey
		buffer.putInt(PDUConst.TAG_CODE_CLIENT_MSG_KEY);
		buffer.putShort((short)(clientMsgKey.length() + 1));
		buffer.put(ByteUtil.addNull(clientMsgKey).getBytes());

		// rsMsgKey	
		if (rsMsgKey !=null && rsMsgKey.length()>0){
			buffer.putInt(PDUConst.TAG_CODE_RS_MSG_KEY);
			buffer.putShort((short)(rsMsgKey.length() + 1));
			buffer.put(ByteUtil.addNull(rsMsgKey).getBytes());
		}
		
		if(attachFileKeys != null && attachFileKeys.size()>0){
			lengthTemp = attachFileKeys.getLength();
			buffer.put(attachFileKeys.encode(lengthTemp).array());
		}
			
		// position reset and write bodyLength
		int tempPos = buffer.position();
		buffer.reset();
		buffer.putInt(tempPos - 8);
		buffer.position(tempPos);
		buffer.limit(buffer.position());
		return ByteUtil.ByteBufferToByte(buffer);
	}

	public int headLength() {
		int length = 0;
		length += PDUConst.SIZE_HEADER_TYPE;
		length += PDUConst.SIZE_HEADER_LENGTH;
		return length;
	}

	private void checkValidation() throws PduException {
		// check mandatory
		if (pduType == 0){
			throw new PduException("0", "pduType is null");
		}

		if (clientMsgKey == null || clientMsgKey.length() <= 0){
			throw new PduException("0", "clientMsgKey is null");
		}
	}
	
	public PDUGwMtRes (){
		
	}

	public int getResCode() {
		return resCode;
	}

	public void setResCode(int resCode) {
		this.resCode = resCode;
	}

	public int getPduType() {
		return pduType;
	}

	public void setPduType(int pduType) {
		this.pduType = pduType;
	}

	public int getMsgClass() {
		return msgClass;
	}

	public void setMsgClass(int msgClass) {
		this.msgClass = msgClass;
	}

	public String getClientMsgKey() {
		return clientMsgKey;
	}

	public void setClientMsgKey(String clientMsgKey) {
		this.clientMsgKey = clientMsgKey;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getCallBack() {
		return callBack;
	}

	public void setCallBack(String callBack) {
		this.callBack = callBack;
	}

	public String getRsMsgKey() {
		return rsMsgKey;
	}

	public void setRsMsgKey(String rsMsgKey) {
		this.rsMsgKey = rsMsgKey;
	}

	public String getRsID() {
		return rsID;
	}

	public void setRsID(String rsID) {
		this.rsID = rsID;
	}
	

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	public int getTsCode() {
		return tsCode;
	}

	public void setTsCode(int tsCode) {
		this.tsCode = tsCode;
	}

	public void setNumPorted(String numPorted) {
		this.numPorted = numPorted;
	}

	public String getRouteGroup() {
		return routeGroup;
	}

	public void setRouteGroup(String routeGroup) {
		this.routeGroup = routeGroup;
	}

	public int getWordCnt() {
		return wordCnt;
	}

	public void setWordCnt(int wordCnt) {
		this.wordCnt = wordCnt;
	}

	public long getTtl() {
		return ttl;
	}

	public void setTtl(long ttl) {
		this.ttl = ttl;
	}
	public String getNumPorted() {
		return numPorted;
	}

	public void setNumPorted(char numPorted) {
		this.numPorted = String.valueOf(numPorted);
	}

	public String getBridgeTag() {
		return bridgeTag;
	}

	public void setBridgeTag(String bridgeTag) {
		this.bridgeTag = bridgeTag;
	}

	public String getEmmaTag() {
		return emmaTag;
	}

	public void setEmmaTag(String emmaTag) {
		this.emmaTag = emmaTag;
	}
		
	public Recipients getRecipients() {
		return recipients;
	}

	public void setRecipients(Recipients recipients) {
		this.recipients = recipients;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public AttachFileKeys getAttachFileKeys() {
		return attachFileKeys;
	}

	public void setAttachFileKeys(AttachFileKeys attachFileKeys) {
		this.attachFileKeys = attachFileKeys;
	}
	
}
