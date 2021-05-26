package br.com.siswbrasil.jee01;

import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.faces.context.FacesContext;

@Dependent
public class Resources {

	@Produces
	public Logger getLogger(InjectionPoint p) {
		return Logger.getLogger(p.getMember().getDeclaringClass().getName());
	}

	@Produces
	@RequestScoped
	public FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

}
