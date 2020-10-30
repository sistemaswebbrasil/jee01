package br.com.siswbrasil.jee01.exception;

import javax.management.RuntimeErrorException;

public class DataBaseException extends RuntimeErrorException {

	private static final long serialVersionUID = 1L;

	public DataBaseException(Error message) {
		super(message);
	}	

}
