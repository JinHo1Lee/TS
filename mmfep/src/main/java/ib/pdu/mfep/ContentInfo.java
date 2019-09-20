package ib.pdu.mfep;

import ib.pdu.mfep.define.MFEPConst;
import ib.pdu.util.ByteUtil;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class ContentInfo implements Serializable {

	private static final long serialVersionUID = 5191757742886022739L;
	private int contentType;

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	private String content;

	public int getLength() {
		int len = 0;

		len += MFEPConst.SIZE_INT;
		len += MFEPConst.SIZE_INT;
		len += content.getBytes().length + 1;
		return len;
	}

	public ByteBuffer encode(int size) {
		ByteBuffer buffer = ByteBuffer.allocate(size);
		buffer.putInt(MFEPConst.CONTENT_TYPE_GENERAL);
		buffer.putInt(content.getBytes().length + 1);
		buffer.put(ByteUtil.addNull(content).getBytes());
		return buffer;
	}
}
