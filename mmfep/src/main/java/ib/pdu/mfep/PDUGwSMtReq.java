package ib.pdu.mfep;

import ib.pdu.mfep.exception.PduException;
import ib.pdu.util.ByteUtil;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PDUGwSMtReq extends PDUGwMtReq implements Serializable{

	private static final long serialVersionUID = -7182142576691890290L;
	
	@Override
	public String toString() {		
		String outStr = "";		
		for (int i=0; i<recipients.size(); i++){
			RecipientInfo recipientInfo = recipients.get(i);
			String recpientFormat = recipientInfo.getRecipientOrder()+"_"+recipientInfo.getRecipientNum()+"_"+recipientInfo.getConuntryCode();
			outStr += String.format("tsCode:%s, routeGroup:%s, clientCode:%s, netId:%d, msgType:%s, carrierKey:%s, clientMsgKey:%s, rsMsgKey:%s, rsID:%s, priority:%s, CallBack:%s, Recpient:%s, npsend:%s, ttl:%s, bridgeTag:%s, emmaTag:%s",
									 tsCode, routeGroup, clientCode, carrier, msgType, carrierKey, clientMsgKey, rsMsgKey, rsID, priority, callBack, recpientFormat, recipients.get(0).getRecipientNpSend(), ttl, bridgeTag, emmaTag);
			if (i>0)outStr +="\n";
		}
		return outStr;
	}
	/*RS Data*/
	private int pduType;
	private int msgClass;
	private String clientMsgKey;
	private String content;
	private int msgType;
	private String callBack;
	private Recipients recipients;
	private String rsMsgKey;
	private String rsID;
	private String clientCode;
	private String routeGroup;
	private int tsCode;

	public byte[] encode() throws PduException {

		checkValidation();

		int lengthTemp = 0;
		ByteBuffer buffer = ByteBuffer.allocate(1024 * 8);

		//pduType
		buffer.putInt(pduType);
		
		buffer.mark(); // mark and write bodyLength temporally
		buffer.putInt(0);
		
		// clientMsgKey
		buffer.putInt(PDUConst.TAG_CODE_CLIENT_MSG_KEY);
		buffer.putShort((short)(clientMsgKey.length() + 1));
		buffer.put(ByteUtil.addNull(clientMsgKey).getBytes());
		
		//Content
		buffer.putInt(PDUConst.TAG_CODE_CONTENT);
		buffer.putInt(content.getBytes().length+1);
		buffer.put(ByteUtil.addNull(content).getBytes());
		
		//Callback
		buffer.putInt(PDUConst.TAG_CODE_CALLBACK);
		buffer.putShort((short)(callBack.length()+1));
		buffer.put(ByteUtil.addNull(callBack).getBytes());
	
		//Recipients
		buffer.putInt(PDUConst.TAG_CODE_RECIPIENTS);
		lengthTemp = recipients.getLength();
		buffer.putInt(lengthTemp);
		buffer.put(recipients.encode(lengthTemp).array());
		
		//RsMsgKey
		buffer.putInt(PDUConst.TAG_CODE_RS_MSG_KEY);
		buffer.putShort((short)(rsMsgKey.length() + 1));
		buffer.put(ByteUtil.addNull(rsMsgKey).getBytes());
		
		//RS ID
		byte[] tempbuffer = new byte[20];
		try{
			System.arraycopy(rsID.getBytes(), 0, tempbuffer, 0, rsID.getBytes().length);
		}catch (Exception e){
			throw new PduException();
		}
		buffer.put(tempbuffer);
		
		//Client Code
		buffer.putInt(Integer.parseInt(clientCode));
		
		//Route Group Code
		buffer.putInt(PDUConst.TAG_CODE_ROUTE_GROUP);
		buffer.putShort((short)(routeGroup.length()+1));
		buffer.put(ByteUtil.addNull(routeGroup).getBytes());
		
		//TS Code
		buffer.putInt(tsCode);
		
		//Message Type
		buffer.putInt(PDUConst.TAG_CODE_MSG_TYPE);
		buffer.putShort((short)msgType);
		
		super.encode(buffer);
		
		// position reset and write bodyLength
		int tempPos = buffer.position();
		buffer.reset();
		buffer.putInt(tempPos - 8);
		buffer.position(tempPos);
		buffer.limit(buffer.position());
		
		return ByteUtil.ByteBufferToByte(buffer);
	}

	private void checkValidation() throws PduException {
		// check mandatory
		if (pduType == 0){
			throw new PduException("0", "pduType is null");
		}
		if (rsMsgKey == null || rsMsgKey.length() <= 0) {
			throw new PduException("0", "rsMsgKey is null");
		}
		if (clientMsgKey == null || clientMsgKey.length() <= 0){
			throw new PduException("0", "clientMsgKey is null");
		}
	}
	
	public PDUGwSMtReq (){
		
	}
	
	public PDUGwSMtReq (byte[] buffer) throws PduException, UnsupportedEncodingException{
		int tc=0, datalen, pos = 0;
		pduType = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		
		int bodyLen = ByteUtil.getint(buffer, 4);
		pos += PDUConst.SIZE_INT;
		
		//CLIENT_MSG_KEY
		tc = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		if (tc == PDUConst.TAG_CODE_CLIENT_MSG_KEY){
			datalen = ByteUtil.getshort(buffer, pos);
			pos += PDUConst.SIZE_SHORT;
			clientMsgKey = new String (ByteUtil.getbytes(buffer, pos, datalen)).trim();
			pos += datalen;	
		}else{
			throw new PduException("0", "PDU decode Exception");
		}
		//CONTENT
		tc = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		if (tc == PDUConst.TAG_CODE_CONTENT){
			datalen = ByteUtil.getint(buffer, pos);
			pos += PDUConst.SIZE_INT;
			
			content = new String (ByteUtil.getbytes(buffer, pos, datalen)).trim();
			
			pos += datalen;
		}else{
			throw new PduException("0", "PDU decode Exception");
		}
		
		//CALLBACK
		tc = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		if (tc == PDUConst.TAG_CODE_CALLBACK){
			datalen = ByteUtil.getshort(buffer, pos);
			pos += PDUConst.SIZE_SHORT;
			callBack = new String (ByteUtil.getbytes(buffer, pos, datalen)).trim();
			pos += datalen;
		}else {
			throw new PduException("0", "PDU decode Exception");
		}
		
		//RECIPIENTS
		tc = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		if (tc ==PDUConst.TAG_CODE_RECIPIENTS){
			datalen = ByteUtil.getint(buffer, pos);
			pos += PDUConst.SIZE_INT;
			recipients = getRecipients(buffer, pos);
			pos += datalen;
		}else{
			throw new PduException("0", "PDU decode Exception");
		}
		//RS MSG KEY
		tc = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		if (tc ==PDUConst.TAG_CODE_RS_MSG_KEY){
			datalen = ByteUtil.getshort(buffer, pos);
			pos += PDUConst.SIZE_SHORT;
			rsMsgKey = new String (ByteUtil.getbytes(buffer, pos, datalen)).trim();
			pos += datalen;
		}else{
			throw new PduException("0", "PDU decode Exception");
		}
		
		//RS ID
		rsID = new String (ByteUtil.getbytes(buffer, pos, 20)).trim();
		pos += 20;
		
		//Client Code
		clientCode = String.format("%08d", ByteUtil.getint(buffer, pos));
		pos += PDUConst.SIZE_INT;
		
		//Route Group Code
		tc = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		if (tc == PDUConst.TAG_CODE_ROUTE_GROUP){
			datalen = ByteUtil.getshort(buffer, pos);
			pos += PDUConst.SIZE_SHORT;
			routeGroup = new String (ByteUtil.getbytes(buffer, pos, datalen)).trim();
			pos += datalen;
		}else{
			throw new PduException("0", "PDU decode Exception");
		}
		
		//TS Code
		tsCode = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		
		//Msg Type
		tc = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		if (tc == PDUConst.TAG_CODE_MSG_TYPE){
			msgType = ByteUtil.getshort(buffer, pos);
			pos += PDUConst.SIZE_SHORT;
		}else{
			throw new PduException("0", "PDU decode Exception");
		}
		
		super.decode(buffer, bodyLen, pos);
	}
	
	public PDUGwMtRes makePduGwRes(int resCode){
		PDUGwMtRes pduRes = new PDUGwMtRes();
		pduRes.setTsCode(tsCode);
		pduRes.setRouteGroup(routeGroup);
		pduRes.setClientCode(clientCode);
		pduRes.setMsgType(msgType);
		pduRes.setRsID(rsID);
		pduRes.setPriority(priority);
		pduRes.setCallBack(callBack);
		pduRes.setRecipients(recipients);
		pduRes.setTtl(ttl);
		pduRes.setBridgeTag(bridgeTag);
		pduRes.setEmmaTag(emmaTag);
		
		pduRes.setPduType(PDUConst.PDU_TYPE_GW_MT_SMS_RES);
		pduRes.setResCode(resCode);
		pduRes.setClientMsgKey(clientMsgKey);
		pduRes.setRsMsgKey(rsMsgKey);
		
		return pduRes;
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

	public String getRouteGroup() {
		return routeGroup;
	}

	public void setRouteGroup(String routeGroup) {
		this.routeGroup = routeGroup;
	}
	
		
	public Recipients getRecipients() {
		return recipients;
	}

	public void setRecipients(Recipients recipients) {
		this.recipients = recipients;
	}

	public String getSubject() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSubject(String subject) {
		// TODO Auto-generated method stub
		
	}

	public String getIbRsltCode() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setIbRsltCode(String ibRsltCode) {
		// TODO Auto-generated method stub
		
	}

	public int getMsgStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setMsgStatus(int msgStatus) {
		// TODO Auto-generated method stub
		
	}

	public String getStrFileKeyList() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setStrFileKeyList(String strFileKeyList) {
		// TODO Auto-generated method stub
		
	}

	public String getStrFileKeyCarrier() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setStrFileKeyCarrier(String strFileKeyCarrier) {
		// TODO Auto-generated method stub
		
	}

}
