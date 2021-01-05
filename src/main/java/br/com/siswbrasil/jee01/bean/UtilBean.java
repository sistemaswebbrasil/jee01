package br.com.siswbrasil.jee01.bean;

import javax.enterprise.inject.Model;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@Model
public class UtilBean {
	
    @Inject
    private FacesContext facesContext;
	
	public void clearMessage() {
		facesContext.getCurrentInstance().getMessages().remove();
	}

}
