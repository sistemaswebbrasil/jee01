package br.com.siswbrasil.jee01.exception;

public class CustomException extends Throwable  {

	private static final long serialVersionUID = 1L;

	public CustomException(Error message) {
		super(message);
	}

}
