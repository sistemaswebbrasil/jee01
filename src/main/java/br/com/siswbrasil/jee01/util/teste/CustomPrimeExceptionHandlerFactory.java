package br.com.siswbrasil.jee01.util.teste;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

import org.primefaces.application.exceptionhandler.PrimeExceptionHandlerFactory;

public class CustomPrimeExceptionHandlerFactory extends PrimeExceptionHandlerFactory {

	public CustomPrimeExceptionHandlerFactory(ExceptionHandlerFactory wrapped) {
		super(wrapped);
	}

	@Override
	public ExceptionHandler getExceptionHandler() {
		return new   CustomPrimeExceptionHandler(getWrapped().getExceptionHandler());
	}

}
