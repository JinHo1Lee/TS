package com.rcs.exception;

public class TokenException extends Exception{
	private static final long serialVersionUID = 1L;
	private String code;
	private String message;
	
	
	public TokenException() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	public TokenException(String code, String message) {
		// TODO Auto-generated constructor stub
		this.code = code;
		this.message = message;
	}
}
