package br.com.siswbrasil.jee01.util.teste;

import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

public class CustomExceptionHandler extends ExceptionHandlerWrapper {

	private ExceptionHandler exceptionHandler;

	public CustomExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return exceptionHandler;
	}

	@Override
	public void handle() throws FacesException {
		final Iterator<ExceptionQueuedEvent> queue = getUnhandledExceptionQueuedEvents().iterator();
		
		System.out.println("Tentando tratar erros");
		
		
		
		
		
		
		System.out.println(queue);
		

		while (queue.hasNext()) {
			System.out.println("Entrei 01");
			System.out.println(queue);
			
			ExceptionQueuedEvent item = queue.next();
			ExceptionQueuedEventContext exceptionQueuedEventContext = (ExceptionQueuedEventContext) item.getSource();
			
			System.out.println(item);

			try {
				System.out.println("Entrei 02");
				Throwable throwable = exceptionQueuedEventContext.getException();
				
				
				System.out.println("*****************************************************************");
				System.out.println("*****************************************************************");
				System.out.println("*****************************************************************");
				System.err.println("Exception: " + throwable.getMessage());
				System.out.println("*****************************************************************");
				System.out.println("*****************************************************************");
				System.out.println("*****************************************************************");
				

				FacesContext context = FacesContext.getCurrentInstance();
				Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
				NavigationHandler nav = context.getApplication().getNavigationHandler();

				requestMap.put("error-message", throwable.getMessage());
				requestMap.put("error-stack", throwable.getStackTrace());
				nav.handleNavigation(context, null, "/error/500.xhtml");
				
				context.renderResponse();
				
				throw new RuntimeException("Agora vou para um pouco", throwable);

			} finally {
				queue.remove();
			}
		}
	}
}
