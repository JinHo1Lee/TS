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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class PDUGwMtReq implements PDUGwMt{
	private static final Logger LOGGER = LoggerFactory.getLogger(PDUGwMtReq.class);
	/*RS Data*/
	protected int contentType;
	protected int wordCnt;
	protected long ttl;
	protected String priority;
	protected String numPorted;
	protected String bridgeTag;
	protected String emmaTag;
	protected int resCode;
	protected int msgClass;
	protected AttachFileKeys attachFileKeys;
	protected int carrier;
	protected long rsRecvTime;
	protected String charset;
	
	// TS Data*/
	private int reSend = 0;
	private long tsRecvTime=0;
	private long tsSendTime=0;
	private String netCode;
	
	protected String carrierKey;
	private long tsRsltRecvTime=0;
	private long tsRsltSendTime=0;
	
	private long tsRsltTime=0;
	
	

	protected byte[] encode(ByteBuffer buffer) throws PduException {		
		int lengthTemp = 0;
		if (carrierKey != null){
			buffer.putInt(PDUConst.TAG_CODE_CARRIER_KEY);
			buffer.putShort((short)(carrierKey.length()+1));
			buffer.put(ByteUtil.addNull(carrierKey).getBytes());
		}
		
		if (wordCnt > 0){
			buffer.putInt(PDUConst.TAG_CODE_WORD_CNT);
			buffer.putInt(wordCnt);
		}
		
		if (ttl > 0){
			buffer.putInt(PDUConst.TAG_CODE_TTL);
			buffer.putInt((int) ttl);
		}
		
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
		
		if(numPorted != null){
			buffer.putInt(PDUConst.TAG_CODE_NUMBER_PORTED);
			byte[] tmpbuffer = new byte[1];
			try{
				System.arraycopy(numPorted.getBytes(), 0, tmpbuffer, 0, numPorted.getBytes().length);
			}catch (Exception e){
				throw new PduException();
			}
			buffer.put(tmpbuffer);
		}
		
		if (msgClass >0){
			buffer.putInt(PDUConst.TAG_CODE_MSG_CLASS);
			buffer.putShort((short)msgClass);
		}
		
		if (bridgeTag != null){
			buffer.putInt(PDUConst.TAG_CODE_BRIDGE_TAG);
			buffer.putShort((short)(bridgeTag.length()+1));
			buffer.put(ByteUtil.addNull(bridgeTag).getBytes());
		}
		
		if (emmaTag != null){
			buffer.putInt(PDUConst.TAG_CODE_EMMA_TAG);
			buffer.putShort((short)(emmaTag.length()+1));
			buffer.put(ByteUtil.addNull(emmaTag).getBytes());
		}

		if(tsRecvTime>0){
			buffer.putInt(PDUConst.TAG_CODE_DATE_TSRECV);
			buffer.putInt((int)tsRecvTime);
		}
		
		if(attachFileKeys != null){
			buffer.putInt(PDUConst.TAG_CODE_ATTACH_FILE_KEYS);
			lengthTemp = attachFileKeys.getLength();
			buffer.putInt(lengthTemp);
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

	protected int headLength() {
		int length = 0;
		length += PDUConst.SIZE_HEADER_TYPE;
		length += PDUConst.SIZE_HEADER_LENGTH;
		return length;
	}
	
	protected PDUGwMtReq (){
		
	}
	
	protected int decode (byte[] buffer, int bodyLen, int pos) throws PduException, UnsupportedEncodingException{
		int tc=0, datalen =0;
		bodyLen += 8;
		while(pos < bodyLen){
			tc = ByteUtil.getint(buffer, pos);
			pos += PDUConst.SIZE_INT;
			switch (tc)
			{
			case PDUConst.TAG_CODE_WORD_CNT:
				wordCnt = ByteUtil.getint(buffer, pos);
				pos += PDUConst.SIZE_INT;
				break;
			case PDUConst.TAG_CODE_TTL:
				ttl = (long)ByteUtil.getint(buffer, pos);
				pos += PDUConst.SIZE_INT;
				break;
			case PDUConst.TAG_CODE_PRIORITY:
				priority = new String (ByteUtil.getbytes(buffer, pos, 2)).trim();
				pos += 2;
				break;
			case PDUConst.TAG_CODE_NUMBER_PORTED:
				numPorted = new String (ByteUtil.getbytes(buffer, pos, 1)).trim();
				pos += 1;
				break;
			case PDUConst.TAG_CODE_MSG_CLASS:
				msgClass =  ByteUtil.getshort(buffer, pos);
				pos += PDUConst.SIZE_SHORT;
				break;
			case PDUConst.TAG_CODE_ATTACH_FILE_KEYS:
				datalen = ByteUtil.getint(buffer, pos);
				pos += PDUConst.SIZE_INT;
				attachFileKeys = getAttachFileKeys(buffer, pos);
				pos += datalen;
				break;
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
			case PDUConst.TAG_CODE_CARRIER_RETRY:
				
				break;
			case PDUConst.TAG_CODE_CARRIER_KEY:
				datalen = ByteUtil.getshort(buffer, pos);
				pos += PDUConst.SIZE_SHORT;
				carrierKey = new String (ByteUtil.getbytes(buffer, pos, datalen)).trim();
				pos += datalen;
				break;
			case PDUConst.TAG_CODE_DATE_TSRECV:
				tsRecvTime = (long)ByteUtil.getint(buffer, pos);
				pos += PDUConst.SIZE_INT;
				break;
			case PDUConst.TAG_CODE_CHARSET:
				charset = new String (ByteUtil.getbytes(buffer, pos, 8)).trim();
				pos += 8;
				break;
			case PDUConst.TAG_CODE_CARRIER:
				carrier = ByteUtil.getint(buffer, pos);
				pos += PDUConst.SIZE_INT;
				break;
			case PDUConst.TAG_CODE_RS_RECV_TIME:
				rsRecvTime = (long)ByteUtil.getint(buffer, pos);
				pos += PDUConst.SIZE_INT;
				break;
			case PDUConst.TAG_CODE_RS_SND_CARRIER:
				carrier = ByteUtil.getint(buffer,pos);
				pos += PDUConst.SIZE_INT;
				break;
			default :
				LOGGER.error("Unkown Tag {}", tc);
				break;
			}
		}
		return pos;
	}
	
	protected AttachFileKeys getAttachFileKeys(byte[] bodyByte, int pos){
		AttachFileKeys attachFileKeys = new AttachFileKeys();
	
		int fileCnt = ByteUtil.getint(bodyByte, pos);
		pos += PDUConst.SIZE_INT;
		
		for (int i=0; i<fileCnt; i++){
			
			int desiredCarrier = ByteUtil.getint(bodyByte, pos);
			pos+=PDUConst.SIZE_INT;
			
			int len = ByteUtil.getshort(bodyByte, pos);
			pos+=PDUConst.SIZE_SHORT;
			
			String fileKey = new String(ByteUtil.getBytes(bodyByte, pos, len)).trim();
			pos+= len;			
			
			AttachFileKeyInfo attachFileKeyInfo = new AttachFileKeyInfo();
			attachFileKeyInfo.setAttachFileKey(fileKey);
			attachFileKeyInfo.setDesiredCarrier(desiredCarrier);
			
			attachFileKeys.add(attachFileKeyInfo);
		}
		return attachFileKeys;
	}

	protected Recipients getRecipients(byte[] bodyByte, int pos){
		Recipients recipients = new Recipients();
		
		
		int recpCnt = ByteUtil.getint(bodyByte, pos);
		pos += PDUConst.SIZE_INT;
		
		for(int i=0; i<recpCnt; i++){
			
			int len = ByteUtil.getint(bodyByte, pos);
			pos += PDUConst.SIZE_INT;
			
			int recipientOrder = ByteUtil.getint(bodyByte, pos);
			pos += PDUConst.SIZE_INT;
			
			int recipientNumSize = ByteUtil.getshort(bodyByte, pos);
			pos += PDUConst.SIZE_SHORT;
			String recipientNum = new String(ByteUtil.getbytes(bodyByte, pos, recipientNumSize)).trim();
			pos += recipientNumSize;
			
			int tc = ByteUtil.getint(bodyByte, pos);
			pos += PDUConst.SIZE_INT;
			String conuntryCode = null;
			short recipientNet = 0;
			if (tc == PDUConst.TAG_CODE_RECIPIENT_NET){
				recipientNet = ByteUtil.getshort(bodyByte, pos);
				pos+=PDUConst.SIZE_SHORT;
			}
			tc = ByteUtil.getint(bodyByte, pos);
			pos += PDUConst.SIZE_INT;
			if (tc == PDUConst.TAG_CODE_COUNTRY_CODE){
				conuntryCode = new String(ByteUtil.getbytes(bodyByte, pos, 8)).trim();
				pos += 8;
			}
			
			tc = ByteUtil.getint(bodyByte, pos);
			pos += PDUConst.SIZE_INT;
			String npSend = null;
			if (tc == PDUConst.TAG_CODE_RECIPIENT_NPSEND){
				npSend = new String(ByteUtil.getbytes(bodyByte, pos, 2)).trim();
				pos += 2;
			}else{
				npSend = "N";
			}
			
			RecipientInfo info = new RecipientInfo();
			info.setRecipientOrder(recipientOrder);
			info.setRecipientNet(recipientNet);
			info.setConuntryCode(conuntryCode);
			info.setRecipientNum(recipientNum);
			info.setRecipientNpSend(npSend);
			
			recipients.add(info);
		}
		
		return recipients;
	}


	public int getResCode() {
		return resCode;
	}

	public void setResCode(int resCode) {
		this.resCode = resCode;
	}
	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}
	
	public void setNumPorted(String numPorted) {
		this.numPorted = numPorted;
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

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getCarrierKey() {
		return carrierKey;
	}

	public void setCarrierKey(String carrierKey) {
		this.carrierKey = carrierKey;
	}

	public int getReSend() {
		return reSend;
	}

	public void setReSend(int reSend) {
		this.reSend = reSend;
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

	public String getNetCode() {
		return netCode;
	}

	public void setNetCode(String netCode) {
		this.netCode = netCode;
	}

	public int getMsgClass() {
		return msgClass;
	}

	public void setMsgClass(int msgClass) {
		this.msgClass = msgClass;
	}

	public AttachFileKeys getAttachFileKeys() {
		return attachFileKeys;
	}

	public void setAttachFileKeys(AttachFileKeys attachFileKeys) {
		this.attachFileKeys = attachFileKeys;
	}

	public long getTsRsltRecvTime() {
		return tsRsltRecvTime;
	}

	public void setTsRsltRecvTime(long tsRsltRecvTime) {
		this.tsRsltRecvTime = tsRsltRecvTime;
	}

	public long getTsRsltSendTime() {
		return tsRsltSendTime;
	}

	public void setTsRsltSendTime(long tsRsltSendTime) {
		this.tsRsltSendTime = tsRsltSendTime;
	}

	public long getTsRsltTime() {
		return tsRsltTime;
	}

	public void setTsRsltTime(long tsRsltTime) {
		this.tsRsltTime = tsRsltTime;
	}
	
	public int getCarrier() {
		return carrier;
	}

	public void setCarrier(int carrier) {
		this.carrier = carrier;
	}
	public int getReportType() {
		return 0;
	}

	public void setReportType(int reportType) {

	}

	public long getRsRecvTime() {
		return rsRecvTime;
	}

	public void setRsRecvTime(long rsRecvTime) {
		this.rsRecvTime = rsRecvTime;
	}
	
}
