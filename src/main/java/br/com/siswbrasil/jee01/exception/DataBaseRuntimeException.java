package br.com.siswbrasil.jee01.exception;

public class DataBaseRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DataBaseRuntimeException(String mensagem) {
		super(mensagem);
	}

	public DataBaseRuntimeException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}

}
