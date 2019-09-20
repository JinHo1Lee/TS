package ib.pdu.mfep;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.apache.ibatis.annotations.Result;

import ib.pdu.mfep.exception.PduException;
import ib.pdu.util.ByteUtil;

public class PDUReportReq extends PDUGwMtReq implements Serializable {
	private static final long serialVersionUID = -7182142576691890290L;
	
	@Override
	public String toString(){
			String recpientFormat = recipientInfo.getRecipientOrder()+"_"+recipientInfo.getRecipientNum()+"_"+recipientInfo.getConuntryCode();
			String result = String.format("tsCode:%s, clientCode:%s, netId:%d, msgType:%s, carrierkey:%s, clientMsgKey:%s, rsMsgKey:%s, rsID:%s, CallBack:%s, Recpient:%s, npsend:%s, ttl:%s, ibRslt:%s, netCode:%s",
										   tsCode, clientCode, carrier, msgType, carrierKey, clientMsgKey, rsMsgKey, rsID, callBack, recpientFormat,  npResult, ttl, ibRsltCode, netCode);
		return result;
	}
	
	protected int pduType;
	protected int reportType;
	protected String ibRsltCode;
	protected String netCode;
	protected String rsID;
	protected String carrierKey;
	protected String clientMsgKey;
	protected String rsMsgKey;
	protected String clientCode;
	
	protected RecipientInfo recipientInfo;
	protected int carrier;
	protected long tsRecvTime=0;
	protected long tsSendTime=0;
	protected long tsRsltRecvTime=0;
	protected long tsRsltSentTime=0;
	protected long tsRsltTime;
	protected int tsCode;
	
	// Option
	protected int wordCnt;
	protected String callBack;
	protected String routeGroup;
	protected String npResult = "N";
	protected String content;
	protected long ttl;
	protected String priority;
	protected String subject;
	protected int msgClass;
	protected int msgType;
	protected AttachFileKeys attachFileKeys;
	protected String emmaTag;
	protected String bridgeTag;
	protected int reportCnt;
	protected long rsRecvTime;
	
	private int msgStatus;
	private String strFileKeyList;
	private String strFileKeyCarrier;
	
	public PDUReportReq(){
		 
	}
	
	public byte[] encode() throws PduException{
		int lengthTemp = 0;
		ByteBuffer buffer = ByteBuffer.allocate(1024 * 8);
		
		//pduType
		buffer.putInt(pduType);
		
		buffer.mark();
		buffer.putInt(0);
		
		//reportType
		buffer.putInt(reportType);
		
		//ib rslt
		byte[] ibrsltbuffer = new byte[4];
		try{
			System.arraycopy(ibRsltCode.getBytes(), 0, ibrsltbuffer, 0, ibRsltCode.getBytes().length);
		}catch (Exception e){
			throw new PduException();
		}
		buffer.put(ibrsltbuffer);

		//net code
		buffer.putShort((short)(netCode.length()+1));
		buffer.put(ByteUtil.addNull(netCode).getBytes());
		
		
		//RS ID
		byte[] rsidbuffer = new byte[20];
		try{
			System.arraycopy(rsID.getBytes(), 0, rsidbuffer, 0, rsID.getBytes().length);
		}catch (Exception e){
			throw new PduException();
		}
		buffer.put(rsidbuffer);
		
		//carrierKey
		buffer.putInt(PDUConst.TAG_CODE_CARRIER_KEY);
		buffer.putShort((short)(carrierKey.getBytes().length+1));
		buffer.put(ByteUtil.addNull(carrierKey).getBytes());
		
		// clientMsgKey
		buffer.putInt(PDUConst.TAG_CODE_CLIENT_MSG_KEY);
		buffer.putShort((short)(clientMsgKey.length() + 1));
		buffer.put(ByteUtil.addNull(clientMsgKey).getBytes());
		
		//RsMsgKey
		buffer.putInt(PDUConst.TAG_CODE_RS_MSG_KEY);
		buffer.putShort((short)(rsMsgKey.length() + 1));
		buffer.put(ByteUtil.addNull(rsMsgKey).getBytes());
		
		//Client Code
		buffer.putInt(Integer.parseInt(clientCode));
		
		//Recipients
		buffer.putInt(PDUConst.TAG_CODE_RECIPIENTS);
		buffer.putInt(recipientInfo.getLength());
		buffer.putInt(recipientInfo.getRecipientOrder());
		buffer.putShort((short) (recipientInfo.getRecipientNum().length() + 1));
		buffer.put(ByteUtil.addNull(recipientInfo.getRecipientNum()).getBytes());
		buffer.putInt(PDUConst.TAG_CODE_RECIPIENT_NET);
		buffer.putShort(recipientInfo.getRecipientNet());
		buffer.putInt(PDUConst.TAG_CODE_COUNTRY_CODE);
		byte[] countryCodebuf = new byte[8];
		System.arraycopy(recipientInfo.getConuntryCode().getBytes(), 0, countryCodebuf, 0, recipientInfo.getConuntryCode().getBytes().length);
		
		buffer.put(countryCodebuf);
		
		buffer.putInt(PDUConst.TAG_CODE_RECIPIENT_NPSEND);
		byte[] npSendbuf = new byte[2];
		System.arraycopy(npResult.getBytes(), 0, npSendbuf, 0, npResult.getBytes().length);
		buffer.put(npSendbuf);
		
		//Carrier
		buffer.putInt(carrier);
		
		//TS Recv
		buffer.putInt((int)tsRecvTime);
		
		//TS Sent
		buffer.putInt((int)tsSendTime);
		
		//TS Rslt Recv
		buffer.putInt((int)tsRsltRecvTime);
		
		//TS Rslt Sent
		buffer.putInt((int)tsRsltSentTime);
		
		//Rslt
		buffer.putInt((int)tsRsltTime);

		//TsCode
		buffer.putInt(tsCode);
		
		//wordCnt
		if (wordCnt > 0){
			buffer.putInt(PDUConst.TAG_CODE_WORD_CNT);
			buffer.putInt(wordCnt);
		}
		
		//CallBack
		if (callBack != null){
			//Callback
			buffer.putInt(PDUConst.TAG_CODE_CALLBACK);
			buffer.putShort((short)(callBack.length()+1));
			buffer.put(ByteUtil.addNull(callBack).getBytes());
		}
		//routeGroup
		if (routeGroup != null){
			buffer.putInt(PDUConst.TAG_CODE_ROUTE_GROUP);
			buffer.putShort((short)(routeGroup.length()+1));
			buffer.put(ByteUtil.addNull(routeGroup).getBytes());
		}
		//np Result
		if(npResult.equals("Y")){
			buffer.putInt(PDUConst.TAG_CODE_NP_RESULT);
			byte[] tmpbuffer = new byte[1];
			try{
				System.arraycopy(npResult.getBytes(), 0, tmpbuffer, 0, npResult.getBytes().length);
			}catch (Exception e){
				throw new PduException();
			}
			buffer.put(tmpbuffer);
		}
		//content
		if (content != null){
			//Content
			buffer.putInt(PDUConst.TAG_CODE_CONTENT);
			buffer.putInt(content.getBytes().length+1);
			buffer.put(ByteUtil.addNull(content).getBytes());
		}
		//ttl
		if (ttl > 0){
			buffer.putInt(PDUConst.TAG_CODE_TTL);
			buffer.putInt((int) ttl);
		}
		//priority
		if (priority != null){
			buffer.putInt(PDUConst.TAG_CODE_PRIORITY);
			byte[] tmpbuffer = new byte[2];
			try{
				System.arraycopy(priority.getBytes(), 0, tmpbuffer, 0, priority.getBytes().length);
			}catch (Exception e){
				throw new PduException();
			}
			buffer.put(tmpbuffer);
		}else{
			priority = "S";
		}
		//subject
		if (subject != null){
			buffer.putInt(PDUConst.TAG_CODE_SUBJECT);
			buffer.putInt(subject.getBytes().length+1);
			buffer.put(ByteUtil.addNull(subject).getBytes());
		}
		//msgClass
		if (msgClass > 0){
			buffer.putInt(PDUConst.TAG_CODE_MSG_CLASS);
			buffer.putShort((short)msgClass);
		}
		//Message Type
		if (msgType > 0){
			buffer.putInt(PDUConst.TAG_CODE_MSG_TYPE);
			buffer.putShort((short)msgType);
		}
		//AttachFileKey
		if(attachFileKeys != null){
			buffer.putInt(PDUConst.TAG_CODE_ATTACH_FILE_KEYS);
			lengthTemp = attachFileKeys.getLength();
			buffer.putInt(lengthTemp);
			buffer.put(attachFileKeys.encode(lengthTemp).array());
		}
		
		if (emmaTag != null){
			buffer.putInt(PDUConst.TAG_CODE_EMMA_TAG);
			buffer.putShort((short)(emmaTag.length()+1));
			buffer.put(ByteUtil.addNull(emmaTag).getBytes());
		}
		if (bridgeTag != null){
			buffer.putInt(PDUConst.TAG_CODE_BRIDGE_TAG);
			buffer.putShort((short)(bridgeTag.length()+1));
			buffer.put(ByteUtil.addNull(bridgeTag).getBytes());
		}
		if (reportCnt > 0){
			buffer.putInt(reportCnt);
		}
		
		int tempPos = buffer.position();
		buffer.reset();
		buffer.putInt(tempPos - 8);
		buffer.position(tempPos);
		buffer.limit(buffer.position());
		
		return ByteUtil.ByteBufferToByte(buffer);
	}
	
	public PDUReportReq (byte[] buffer) throws PduException{
		int tc=0, datalen, pos = 0;
		pduType = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		
		int bodyLen = ByteUtil.getint(buffer, 4);
		pos += PDUConst.SIZE_INT;
		
		//ReportType
		reportType = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		
		//ibrslt
		ibRsltCode = new String (ByteUtil.getbytes(buffer, pos, 4)).trim();
		pos += PDUConst.SIZE_INT;
		
		//NetCode;
		datalen = (int)ByteUtil.getshort(buffer, pos);
		pos += PDUConst.SIZE_SHORT;
		netCode = new String (ByteUtil.getBytes(buffer, pos, datalen)).trim();
		pos += datalen;
		
		//RS ID
		rsID = new String (ByteUtil.getbytes(buffer, pos, 20)).trim();
		pos += 20;
		
		//Carrier Key
		tc = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		if (tc == PDUConst.TAG_CODE_CARRIER_KEY){
			datalen = ByteUtil.getshort(buffer, pos);
			pos += PDUConst.SIZE_SHORT;
			carrierKey = new String (ByteUtil.getbytes(buffer, pos, datalen)).trim();
			pos += datalen;
		}else{
			throw new PduException("0", "PDU decode Exception");
		}
		
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
		
		//Client Code
		clientCode = String.format("%08d", ByteUtil.getint(buffer, pos));
		pos += PDUConst.SIZE_INT;
		
		//RECIPIENTS
		tc = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		if (tc ==PDUConst.TAG_CODE_RECIPIENTS){
			recipientInfo = new RecipientInfo();
			datalen = ByteUtil.getint(buffer, pos);
			pos+=PDUConst.SIZE_INT;
			if (datalen > 0){
				recipientInfo.setRecipientOrder(ByteUtil.getint(buffer, pos));
				pos += PDUConst.SIZE_INT;
				datalen = ByteUtil.getshort(buffer, pos);
				pos += PDUConst.SIZE_SHORT;
				recipientInfo.setRecipientNum(new String(ByteUtil.getBytes(buffer, pos, datalen)).trim());
				pos += datalen;
				tc = ByteUtil.getint(buffer, pos);
				pos += PDUConst.SIZE_INT;
				if (tc == PDUConst.TAG_CODE_RECIPIENT_NET){
					recipientInfo.setRecipientNet(ByteUtil.getshort(buffer, pos));
					pos += PDUConst.SIZE_SHORT;
				}
				tc = ByteUtil.getint(buffer, pos);
				pos += PDUConst.SIZE_INT;
				if (tc == PDUConst.TAG_CODE_COUNTRY_CODE){
					recipientInfo.setConuntryCode(new String(ByteUtil.getbytes(buffer, pos, 8)).trim());
					pos += 8;
				}
				
				tc = ByteUtil.getint(buffer, pos);
				pos += PDUConst.SIZE_INT;
				if (tc == PDUConst.TAG_CODE_RECIPIENT_NPSEND){
					recipientInfo.setRecipientNpSend(new String(ByteUtil.getbytes(buffer, pos, 2)).trim());
					pos += 2;
				}
				
			}
		}else{
			throw new PduException("0", "PDU decode Exception");
		}
		
		//Carrier
		carrier = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		//TS Recv
		tsRecvTime = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		//TSSent
		tsSendTime = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		//TSRsltRescv
		tsRsltRecvTime = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		//TSRsltSent
		tsRsltSentTime = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		//Rslt
		tsRsltTime = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		//TsCode
		tsCode = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		
		while(pos < bodyLen){
			tc = ByteUtil.getint(buffer, pos);
			pos += PDUConst.SIZE_INT;
			switch (tc)
			{
			case PDUConst.TAG_CODE_WORD_CNT:
				wordCnt = ByteUtil.getint(buffer, pos);
				pos += PDUConst.SIZE_INT;
				break;
			case PDUConst.TAG_CODE_CALLBACK:
				datalen = ByteUtil.getshort(buffer, pos);
				pos += PDUConst.SIZE_SHORT;
				callBack = new String (ByteUtil.getbytes(buffer, pos, datalen)).trim();
				pos += datalen;
				break;
			case PDUConst.TAG_CODE_ROUTE_GROUP:
				datalen = ByteUtil.getshort(buffer, pos);
				pos += PDUConst.SIZE_SHORT;
				routeGroup = new String (ByteUtil.getbytes(buffer, pos, datalen)).trim();
				pos += datalen;
				break;
			case PDUConst.TAG_CODE_NP_RESULT:
				npResult = new String (ByteUtil.getbytes(buffer, pos, 1)).trim();
				pos += 1;
				break;
			case PDUConst.TAG_CODE_CONTENT:
				datalen = ByteUtil.getint(buffer, pos);
				pos += PDUConst.SIZE_INT;
				content = new String (ByteUtil.getbytes(buffer, pos, datalen)).trim();
				pos += datalen;
				break;
			case PDUConst.TAG_CODE_TTL:
				ttl = (long)ByteUtil.getint(buffer, pos);
				pos += PDUConst.SIZE_INT;
				break;
			case PDUConst.TAG_CODE_PRIORITY:
				priority = new String (ByteUtil.getbytes(buffer, pos, 2)).trim();
				pos += 2;
				break;
			case PDUConst.TAG_CODE_SUBJECT:
				datalen = ByteUtil.getint(buffer, pos);
				pos+=PDUConst.SIZE_INT;
				subject = new String(ByteUtil.getBytes(buffer, pos, datalen)).trim();
				pos += datalen;
				break;
			case PDUConst.TAG_CODE_MSG_CLASS:
				msgClass =  ByteUtil.getint(buffer, pos);
				pos += PDUConst.SIZE_INT;
				break;
			case PDUConst.TAG_CODE_MSG_TYPE:
				msgType = ByteUtil.getint(buffer, pos);
				pos += PDUConst.SIZE_INT;
				break;
			case PDUConst.TAG_CODE_ATTACH_FILE_KEYS:
				datalen = ByteUtil.getint(buffer, pos);
				pos += PDUConst.SIZE_INT;
				attachFileKeys = getAttachFileKeys(buffer, pos);
				pos += datalen;
			case PDUConst.TAG_CODE_EMMA_TAG:
				datalen = ByteUtil.getshort(buffer, pos);
				pos += PDUConst.SIZE_SHORT;
				emmaTag = new String (ByteUtil.getbytes(buffer, pos, datalen)).trim();
				pos += datalen;
				break;
			case PDUConst.TAG_CODE_BRIDGE_TAG:
				datalen = ByteUtil.getshort(buffer, pos);
				pos += PDUConst.SIZE_SHORT;
				bridgeTag = new String (ByteUtil.getbytes(buffer, pos, datalen)).trim();
				pos += datalen;
				break;
			default :
				System.out.println("Unknown : "  +tc);
				break;
			}
		}
	}


	public int getPduType() {
		return pduType;
	}

	public void setPduType(int pduType) {
		this.pduType = pduType;
	}

	public int getReportType() {
		return reportType;
	}

	public void setReportType(int reportType) {
		this.reportType = reportType;
	}

	public String getIbRsltCode() {
		return ibRsltCode;
	}

	public void setIbRsltCode(String ibRsltCode) {
		this.ibRsltCode = ibRsltCode;
	}

	public String getNetCode() {
		return netCode;
	}

	public void setNetCode(String netCode) {
		this.netCode = netCode;
	}

	public String getRsID() {
		return rsID;
	}

	public void setRsID(String rsID) {
		this.rsID = rsID;
	}

	public String getCarrierKey() {
		return carrierKey;
	}

	public void setCarrierKey(String carrierKey) {
		this.carrierKey = carrierKey;
	}

	public String getClientMsgKey() {
		return clientMsgKey;
	}

	public void setClientMsgKey(String clientMsgKey) {
		this.clientMsgKey = clientMsgKey;
	}

	public String getRsMsgKey() {
		return rsMsgKey;
	}

	public void setRsMsgKey(String rsMsgKey) {
		this.rsMsgKey = rsMsgKey;
	}

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	public RecipientInfo getRecipientInfo() {
		return recipientInfo;
	}

	public void setRecipientInfo(RecipientInfo recipientInfo) {
		this.recipientInfo = recipientInfo;
	}

	public int getCarrier() {
		return carrier;
	}

	public void setCarrier(int carrier) {
		this.carrier = carrier;
	}

	public long getTsRecvTime() {
		return tsRecvTime;
	}

	public void setTsRecvTime(long tsRecvTime) {
		this.tsRecvTime = tsRecvTime;
	}

	public long getTsSendTime() {
		return tsSendTime;
	}

	public void setTsSendTime(long tsSendTime) {
		this.tsSendTime = tsSendTime;
	}

	public long getTsRsltRecvTime() {
		return tsRsltRecvTime;
	}

	public void setTsRsltRecvTime(long tsRsltRecvTime) {
		this.tsRsltRecvTime = tsRsltRecvTime;
	}

	public long getTsRsltSentTime() {
		return tsRsltSentTime;
	}

	public void setTsRsltSentTime(long tsRsltSentTime) {
		this.tsRsltSentTime = tsRsltSentTime;
	}

	public long getTsRsltTime() {
		return tsRsltTime;
	}

	public void setTsRsltTime(long tsRsltTime) {
		this.tsRsltTime = tsRsltTime;
	}

	public int getTsCode() {
		return tsCode;
	}

	public void setTsCode(int tsCode) {
		this.tsCode = tsCode;
	}

	public int getWordCnt() {
		return wordCnt;
	}

	public void setWordCnt(int wordCnt) {
		this.wordCnt = wordCnt;
	}

	public String getCallBack() {
		return callBack;
	}

	public void setCallBack(String callBack) {
		this.callBack = callBack;
	}

	public String getRouteGroup() {
		return routeGroup;
	}

	public void setRouteGroup(String routeGroup) {
		this.routeGroup = routeGroup;
	}

	public String getNpResult() {
		return npResult;
	}

	public void setNpResult(String npResult) {
		this.npResult = npResult;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getTtl() {
		return ttl;
	}

	public void setTtl(long ttl) {
		this.ttl = ttl;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getMsgClass() {
		return msgClass;
	}

	public void setMsgClass(int msgClass) {
		this.msgClass = msgClass;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public AttachFileKeys getAttachFileKeys() {
		return attachFileKeys;
	}

	public void setAttachFileKeys(AttachFileKeys attachFileKeys) {
		this.attachFileKeys = attachFileKeys;
	}

	public String getEmmaTag() {
		return emmaTag;
	}

	public void setEmmaTag(String emmaTag) {
		this.emmaTag = emmaTag;
	}

	public String getBridgeTag() {
		return bridgeTag;
	}

	public void setBridgeTag(String bridgeTag) {
		this.bridgeTag = bridgeTag;
	}

	public int getReportCnt() {
		return reportCnt;
	}

	public void setReportCnt(int reportCnt) {
		this.reportCnt = reportCnt;
	}

	public PDUReportReq (PDUGwMt pduGwMt){
		//mandantory
		pduType = pduGwMt.getPduType();
		reportType = pduGwMt.getMsgType();
		
		ibRsltCode = pduGwMt.getIbRsltCode();
		netCode = pduGwMt.getNetCode();
		rsID = pduGwMt.getRsID();
		
		carrierKey = pduGwMt.getCarrierKey();
		clientMsgKey = pduGwMt.getClientMsgKey();
		rsMsgKey = pduGwMt.getRsMsgKey();
		clientCode = pduGwMt.getClientCode();
		recipientInfo = pduGwMt.getRecipients().get(0);
		tsRecvTime = pduGwMt.getTsRecvTime();
		tsSendTime = pduGwMt.getTsSendTime();
		tsRsltRecvTime = pduGwMt.getTsRsltRecvTime();
		tsRsltSentTime = pduGwMt.getTsRsltSendTime();
		tsRsltTime = pduGwMt.getTsRsltTime();
		tsCode = pduGwMt.getTsCode();
		
		//options
		subject = pduGwMt.getSubject();
		content = pduGwMt.getContent();
		wordCnt = pduGwMt.getWordCnt();
		priority = pduGwMt.getPriority();
		routeGroup = pduGwMt.getRouteGroup();
		callBack = pduGwMt.getCallBack();
		numPorted = pduGwMt.getNumPorted();
		msgType = pduGwMt.getMsgType();
		msgClass = pduGwMt.getMsgClass();
		ttl = pduGwMt.getTtl();
		tsRecvTime = pduGwMt.getTsRecvTime();
		tsSendTime = pduGwMt.getTsSendTime();
		tsRsltRecvTime = pduGwMt.getTsRsltRecvTime();
		tsRsltSentTime = pduGwMt.getTsRsltSendTime();
		npResult = pduGwMt.getRecipients().get(0).getRecipientNpSend();
		tsRsltTime = pduGwMt.getTsRsltTime();
		emmaTag = pduGwMt.getEmmaTag();
		bridgeTag = pduGwMt.getBridgeTag();
		contentType = pduGwMt.getContentType();
		carrier = pduGwMt.getCarrier();
		rsRecvTime = pduGwMt.getRsRecvTime();
	}

	public Recipients getRecipients() {
		// TODO Auto-generated method stub
		Recipients recipients = new Recipients();
		recipients.add(recipientInfo);
		return recipients;
	}

	public void setRecipients(Recipients recipients) {
		// TODO Auto-generated method stub
		
	}

	public int getMsgStatus() {
		return msgStatus;
	}
	
	public void setMsgStatus(int msgStatus) {
		this.msgStatus = msgStatus;
	}
	
	public String getStrFileKeyList() {
		return strFileKeyList;
	}
	
	public void setStrFileKeyList(String strFileKeyList) {
		this.strFileKeyList = strFileKeyList;
	}
	
	public String getStrFileKeyCarrier() {
		return strFileKeyCarrier;
	}
	
	public void setStrFileKeyCarrier(String strFileKeyCarrier) {
		this.strFileKeyCarrier = strFileKeyCarrier;
	}

	public long getRsRecvTime() {
		return rsRecvTime;
	}

	public void setRsRecvTime(long rsRecvTime) {
		this.rsRecvTime = rsRecvTime;
	}
	
	
}
