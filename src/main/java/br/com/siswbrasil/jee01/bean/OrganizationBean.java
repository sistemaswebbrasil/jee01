package br.com.siswbrasil.jee01.bean;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;

import br.com.siswbrasil.jee01.model.Organization;
import br.com.siswbrasil.jee01.service.OrganizationService;


@Getter
@Setter
@Named
@RequestScoped
public class OrganizationBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private OrganizationService service;
	
    @Inject
    private FacesContext facesContext;
    	
	private Long organizationId;
	private Organization organization;
    
    public void init() {    
        if (organizationId == null) {
            organization = new Organization();
        } else {
	        FacesMessage info = new FacesMessage( FacesMessage.SEVERITY_INFO, "Erro", "Registro não encontrado");
	        facesContext.addMessage(null, info);
        }
    }
    
    public String save() throws Throwable {
    	if (organization.getId() == null) {
            service.create(organization);
        } else {
            service.update(organization);
        }
        FacesMessage info = new FacesMessage( "Registro salvo com sucesso!!");
        facesContext.addMessage(null, info);
        return "/index.xhtml?faces-redirect=true";
    }  

}
