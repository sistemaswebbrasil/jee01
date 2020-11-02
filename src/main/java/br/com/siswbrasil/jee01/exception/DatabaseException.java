package br.com.siswbrasil.jee01.exception;

public class DatabaseException extends BusinessException {

	private static final long serialVersionUID = 1L;

	public DatabaseException(String message, String detail, Throwable cause) {
		super(message, detail, cause);	
	}

	public DatabaseException(String message, String detail) {
		super(message, detail);
	}

	public DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public DatabaseException(String message) {
		super(message);
	}


}
