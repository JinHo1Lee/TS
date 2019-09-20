package ib.pdu.mfep;

import java.nio.ByteBuffer;

import ib.pdu.mfep.define.MFEPConst;
import ib.pdu.mfep.exception.PduException;
import ib.pdu.util.ByteUtil;

public class AttachFileKeyInfo {
	private int desiredCarrier;
	private String attachFileKey;
	private int length;

	public int getLength() {
		int len = 0;
		len += MFEPConst.SIZE_INT; //desiredCarrier
		len += MFEPConst.SIZE_SHORT; //key len
		len += attachFileKey.getBytes().length + 1;
		return len;
	}

	public ByteBuffer encode(int size) throws PduException{
		ByteBuffer buffer = ByteBuffer.allocate(size);
		buffer.putInt(0);
		buffer.putShort((short)(attachFileKey.getBytes().length+1));
		buffer.put(ByteUtil.addNull(attachFileKey).getBytes());
		return buffer;
	}

	public int getDesiredCarrier() {
		return desiredCarrier;
	}

	public void setDesiredCarrier(int desiredCarrier) {
		this.desiredCarrier = desiredCarrier;
	}

	public String getAttachFileKey() {
		return attachFileKey;
	}

	public void setAttachFileKey(String attachFileKey) {
		this.attachFileKey = attachFileKey;
	}
	
	
}
