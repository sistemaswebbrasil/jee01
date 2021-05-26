package br.com.siswbrasil.jee01.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import javax.annotation.PostConstruct;

import br.com.siswbrasil.jee01.exception.DatabaseException;
import br.com.siswbrasil.jee01.util.MessageUtil;
import br.com.siswbrasil.jee01.model.Organization;
import br.com.siswbrasil.jee01.service.OrganizationService;
import br.com.siswbrasil.jee01.datamodel.OrganizationDataModel;


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
    
    @Inject
    private OrganizationDataModel dataModel;    
    	
	private Long organizationId;
	private Organization organization;
    
    @PostConstruct
    public void init() throws IOException {    
        if (organizationId == null) {
            organization = new Organization();
        } else {
			organization = service.findById(organizationId);
			if (organization == null) {
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("register_not_found"));
				FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
			}			
        }
    }  
    
    public String save() throws Throwable {
    	if (organization.getId() == null) {
            service.create(organization);
        } else {
            service.update(organization);
        }
        
        MessageUtil.addSuccessMessage(MessageUtil.getMsg("success"), MessageUtil.getMsg("create_success"));        
        return "index.xhtml?faces-redirect=true";
    }
    
	public void delete(Long organizationId) {
		try {
			service.deleteById(organizationId);
			MessageUtil.addSuccessMessage(MessageUtil.getMsg("success"), MessageUtil.getMsg("delete_success"));
		} catch (Exception e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("delete_fail"));							
		}
	}

}
