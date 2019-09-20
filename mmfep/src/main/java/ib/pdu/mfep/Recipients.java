package ib.pdu.mfep;

import ib.pdu.mfep.define.MFEPConst;
import ib.pdu.mfep.exception.PduException;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Recipients extends ArrayList<RecipientInfo> implements Serializable{

		private static final long serialVersionUID = 1L;

		public int getLength() {
			int len = 0;
			len += MFEPConst.SIZE_INT; //recipient_cnt field size
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
				RecipientInfo recipientInfo = this.get(i);
				
				int tLen = recipientInfo.getLength();
				ByteBuffer tBuffer = recipientInfo.encode(tLen);
				buffer.putInt(tLen);
				buffer.put(tBuffer.array());
			}
			return buffer;
		}

		public ArrayList<RecipientInfo> getRecipientInfoList() {
			return this;
		}

	}