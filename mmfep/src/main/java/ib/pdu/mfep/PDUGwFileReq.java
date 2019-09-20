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

import javax.swing.text.Position.Bias;

public class PDUGwFileReq {

	private static final long serialVersionUID = -7182142576691890290L;
	
	/*RS Data*/
	private int pduType;
	private String clientMsgKey;
	private AttachFiles attachFiles;
	
	public PDUGwFileReq (){
		
	}
	
	public PDUGwFileReq (byte[] buffer) throws PduException, UnsupportedEncodingException{
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
		
		tc = ByteUtil.getint(buffer, pos);
		pos += PDUConst.SIZE_INT;
		if (tc == PDUConst.TAG_CODE_ATTACH_FILES){
			datalen = ByteUtil.getint(buffer, pos);
			pos +=PDUConst.SIZE_INT;
			attachFiles = getAttachFiles(buffer, pos);
		}else{
			throw new PduException("0", "PDU decode Exception");
		}
	}

	private AttachFiles getAttachFiles(byte[] bodyByte, int pos){
		AttachFiles attachFiles = new AttachFiles();
		
		
		int fileCnt = ByteUtil.getint(bodyByte, pos);
		pos += PDUConst.SIZE_INT;
		
		for (int i=0; i<fileCnt; i++){
			
			int tag = ByteUtil.getint(bodyByte, pos);
			pos += PDUConst.SIZE_INT;
			int len = ByteUtil.getint(bodyByte, pos);
			pos += PDUConst.SIZE_INT;
			
			int desiredCarrier = ByteUtil.getint(bodyByte, pos);
			pos +=PDUConst.SIZE_INT;

			short fileKeySize = ByteUtil.getshort(bodyByte, pos);
			pos += PDUConst.SIZE_SHORT;
			String attachFileKey = new String(ByteUtil.getBytes(bodyByte, pos, fileKeySize)).trim();
			pos += fileKeySize;
			
			short fileNameSize = ByteUtil.getshort(bodyByte, pos);
			pos += PDUConst.SIZE_SHORT;
			String attachFileName = new String(ByteUtil.getbytes(bodyByte, pos, fileNameSize));
			pos += fileNameSize;
			
			int bufSize = ByteUtil.getint(bodyByte, pos);
			pos += PDUConst.SIZE_INT;
			pos += PDUConst.SIZE_INT;
			
			byte []attachFileContent = new byte[bufSize];
			attachFileContent = ByteUtil.getbytes(bodyByte, pos, bufSize);
			pos += bufSize;
			
			AttachFileInfo attachFileInfo = new AttachFileInfo();
			attachFileInfo.setAttachFileSize(bufSize);
			attachFileInfo.setAttachFileKey(attachFileKey);
			attachFileInfo.setAttachFileName(attachFileName);
			attachFileInfo.setAttachFileContent(attachFileContent);

			attachFiles.add(attachFileInfo);
		}
		
		return attachFiles;
	}
	
	public PDUGwMtRes makePduGwRes(int resCode){
		PDUGwMtRes pduRes = new PDUGwMtRes();
		pduRes.setClientMsgKey(clientMsgKey);
		pduRes.setResCode(resCode);
		pduRes.setPduType(PDUConst.PDU_TYPE_GW_MT_FILE_RES);
		return pduRes;
	}
	
	public int getPduType() {
		return pduType;
	}

	public void setPduType(int pduType) {
		this.pduType = pduType;
	}

	public String getClientMsgKey() {
		return clientMsgKey;
	}

	public void setClientMsgKey(String clientMsgKey) {
		this.clientMsgKey = clientMsgKey;
	}

	public AttachFiles getAttachFiles() {
		return attachFiles;
	}

	public void setAttachFiles(AttachFiles attachFiles) {
		this.attachFiles = attachFiles;
	}
}
