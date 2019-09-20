package ib.pdu.mfep;

import java.io.Serializable;
import java.nio.ByteBuffer;

import ib.pdu.mfep.exception.PduException;
import ib.pdu.util.ByteUtil;

public class PDUReportRes implements Serializable {
	private static final long serialVersionUID = -7182142576691890290L;
	
	private int pduType;
	private int resCode;
	private String clientMsgKey;
	private String rsMsgKey;
	
	@Override
	public String toString(){
		String result = String.format("clientMsgKey:%s, rsMsgKey:%s, resCode:%d", clientMsgKey, rsMsgKey, resCode);
		return result;
	}
	
	public PDUReportRes(){
		
	}
	
	
	public PDUReportRes (byte[] buffer) throws PduException{
		int tc=0, datalen, pos = 0;
		pduType = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		
		int bodyLen = ByteUtil.getint(buffer, 4);
		pos += PDUConst.SIZE_INT;
		
		//resCode
		resCode = ByteUtil.getint(buffer, pos);
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
	}


	public int getPduType() {
		return pduType;
	}


	public void setPduType(int pduType) {
		this.pduType = pduType;
	}


	public int getResCode() {
		return resCode;
	}


	public void setResCode(int resCode) {
		this.resCode = resCode;
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

	
}
