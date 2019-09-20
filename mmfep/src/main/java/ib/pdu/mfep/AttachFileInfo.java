package ib.pdu.mfep;

import java.nio.ByteBuffer;

import ib.pdu.mfep.define.MFEPConst;
import ib.pdu.mfep.exception.PduException;
import ib.pdu.util.ByteUtil;

public class AttachFileInfo {
	private String attachFileKey;
	private String attachFileName;
	private int attachFileSize;
	private byte[] attachFileContent;
	
	public int getLength() {
		int len = 0;
		len += MFEPConst.SIZE_SHORT;
		len += attachFileKey.getBytes().length + 1;
		len += MFEPConst.SIZE_SHORT;
		len += attachFileName.getBytes().length + 1;
		len += MFEPConst.SIZE_INT;
		len += MFEPConst.SIZE_INT;
		len += attachFileContent.length;
		return len;
	}

	public ByteBuffer encode(int size) throws PduException{
		ByteBuffer buffer = ByteBuffer.allocate(size);
		
		buffer.putShort((short)(attachFileKey.length() + 1));
		buffer.put(ByteUtil.addNull(attachFileKey).getBytes());
		buffer.putShort((short) (attachFileName.length() + 1));
		buffer.put(ByteUtil.addNull(attachFileName).getBytes());
		buffer.putInt(attachFileSize);
		buffer.putInt(attachFileContent.length);
		buffer.put(attachFileContent);
		return buffer;
	}

	public String getAttachFileKey() {
		return attachFileKey;
	}

	public void setAttachFileKey(String attachFileKey) {
		this.attachFileKey = attachFileKey;
	}

	public String getAttachFileName() {
		return attachFileName;
	}

	public void setAttachFileName(String attachFileName) {
		this.attachFileName = attachFileName;
	}

	public int getAttachFileSize() {
		return attachFileSize;
	}

	public void setAttachFileSize(int attachFileSize) {
		this.attachFileSize = attachFileSize;
	}

	public byte[] getAttachFileContent() {
		return attachFileContent;
	}

	public void setAttachFileContent(byte[] attachFileContent) {
		this.attachFileContent = attachFileContent;
	}
	
}
