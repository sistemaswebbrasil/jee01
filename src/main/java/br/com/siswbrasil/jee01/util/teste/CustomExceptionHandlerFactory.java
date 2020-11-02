package br.com.siswbrasil.jee01.util.teste;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

public class CustomExceptionHandlerFactory extends ExceptionHandlerFactory {

	private ExceptionHandlerFactory exceptionHandlerFactory;

	public CustomExceptionHandlerFactory() {
	}

	public CustomExceptionHandlerFactory(ExceptionHandlerFactory exceptionHandlerFactory) {
		this.exceptionHandlerFactory = exceptionHandlerFactory;
	}

	@Override
	public ExceptionHandler getExceptionHandler() {
		return new CustomExceptionHandler(exceptionHandlerFactory.getExceptionHandler());
	}

}