package br.com.siswbrasil.jee01.exception;

public class BusinessException extends Exception {

	private static final long serialVersionUID = 1L;
	private String detail;

	public BusinessException(String message, String detail, Throwable cause) {
		super(message, cause);
		this.detail = detail;
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessException(String message, String detail) {
		super(message);
		this.detail = detail;
	}

	public BusinessException(String message) {
		super(message);
	}
	
	public String getDetail() {
		return detail;
	}	

}
