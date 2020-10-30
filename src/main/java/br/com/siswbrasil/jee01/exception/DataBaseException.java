package br.com.siswbrasil.jee01.exception;

public class DataBaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DataBaseException(String mensagem) {
		super(mensagem);
	}

	public DataBaseException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}

}
