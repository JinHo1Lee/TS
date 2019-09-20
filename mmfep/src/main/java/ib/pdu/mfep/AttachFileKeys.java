package ib.pdu.mfep;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import ib.pdu.mfep.define.MFEPConst;
import ib.pdu.mfep.exception.PduException;

public class AttachFileKeys extends ArrayList<AttachFileKeyInfo> implements Serializable{
	private static final long serialVersionUID = 4517765259570642892L;

	public int getLength() {
		int len = 0;
		len += MFEPConst.SIZE_INT; //attach_files field size
		for (int i = 0; i < this.size(); i++) {
			len += MFEPConst.SIZE_INT; //L
			len += this.get(i).getLength(); //V
		}
		return len;
	}

	public ByteBuffer encode(int length) throws PduException {
		ByteBuffer buffer = ByteBuffer.allocate(length);
		buffer.putInt(this.size());
		for (int i = 0; i < this.size(); i++) {
			AttachFileKeyInfo attachFileInfo = this.get(i);

			int tLen = attachFileInfo.getLength();
			ByteBuffer tBuffer = attachFileInfo.encode(tLen);
			buffer.put(tBuffer.array());
		}
		return buffer;
	}

	public ArrayList<AttachFileKeyInfo> getRecipientInfoList() {
		return this;
	}
	
}
