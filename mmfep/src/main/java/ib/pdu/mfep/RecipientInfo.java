package ib.pdu.mfep;

import ib.pdu.mfep.define.MFEPConst;
import ib.pdu.mfep.exception.PduException;
import ib.pdu.util.ByteUtil;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class RecipientInfo implements Serializable {

	private static final long serialVersionUID = -5150295132110229137L;
	private int recipientOrder;
	private String recipientNum;
	private String conuntryCode;
	private short recipientNet;
	private String recipientNpSend;
	private int length;

	public short getRecipientNet() {
		return recipientNet;
	}

	public void setRecipientNet(short recipientNet) {
		this.recipientNet = recipientNet;
	}

	public int getRecipientOrder() {
		return recipientOrder;
	}

	public void setRecipientOrder(int recipientOrder) {
		this.recipientOrder = recipientOrder;
	}

	public String getConuntryCode() {
		return conuntryCode;
	}

	public void setConuntryCode(String conuntryCode) {
		this.conuntryCode = conuntryCode;
	}

	public int getLength() {
		int length = 0;
		length += MFEPConst.SIZE_INT;
		length += MFEPConst.SIZE_SHORT;
		length += getRecipientNum().getBytes().length + 1;
		
		length += MFEPConst.SIZE_INT;
		length += MFEPConst.SIZE_SHORT;
		
		length += MFEPConst.SIZE_INT;
		length += 8;
		
		length += MFEPConst.SIZE_INT;
		length += 2;
		return length;
	}

	
	public void setLength(int length) {
		this.length = length;
	}

	public ByteBuffer encode(int size) throws PduException{
		ByteBuffer buffer = ByteBuffer.allocate(size);
		buffer.putInt(recipientOrder);
		buffer.putShort((short) (getRecipientNum().length() + 1));
		buffer.put(ByteUtil.addNull(getRecipientNum()).getBytes());
		buffer.putInt(PDUConst.TAG_CODE_RECIPIENT_NET);
		buffer.putShort(recipientNet);
		buffer.putInt(PDUConst.TAG_CODE_COUNTRY_CODE);
		byte[] coutryCodeBuf = new byte[8];
		System.arraycopy(conuntryCode.getBytes(), 0, coutryCodeBuf, 0, conuntryCode.getBytes().length);
		
		buffer.put(coutryCodeBuf);
		if (recipientNpSend != null){
			buffer.putInt(PDUConst.TAG_CODE_RECIPIENT_NPSEND);
			byte[] npSendBuf = new byte[2];
			System.arraycopy(recipientNpSend.getBytes(), 0, npSendBuf, 0, recipientNpSend.getBytes().length);
			buffer.put(npSendBuf);
		}

		return buffer;
	}

	public String getRecipientNum() {
		return recipientNum;
	}

	public void setRecipientNum(String recipientNum) {
		this.recipientNum = recipientNum;
	}

	public String getRecipientNpSend() {
		return recipientNpSend;
	}

	public void setRecipientNpSend(String recipientNpSend) {
		this.recipientNpSend = recipientNpSend;
	}
	
}