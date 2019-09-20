package ib.pdu.mfep.exception;

public class PduException extends Exception {

	private static final long serialVersionUID = 1L;
	private String code;
	private String message;

	public PduException() {
		super();
	}

	public PduException(String code) {
		this.code = code;
	}

	public PduException(String code, String message) {
		this.message = message;
	}

}
