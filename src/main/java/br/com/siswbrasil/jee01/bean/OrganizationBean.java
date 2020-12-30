package br.com.siswbrasil.jee01.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.siswbrasil.jee01.exception.DatabaseException;
import br.com.siswbrasil.jee01.model.Organization;
import br.com.siswbrasil.jee01.service.OrganizationService;
import lombok.Getter;
import lombok.Setter;

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
	private Logger LOG;

	private Long organizationId;
	private Organization organization;
	private List<Organization> organizationList = new ArrayList<Organization>();

	@PostConstruct
	public void init() {

		LOG.info("--------------------------------------------------------------------");
		LOG.info("Init");

		if (organizationId == null) {
			LOG.info("ID nulo");
			organization = new Organization();
		} else {
			LOG.info("ID não nulo");			
			organization = service.findById(organizationId);
			
			FacesMessage info = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Registro não encontrado");
			//facesContext.addMessage(null, info);

		}
	}

	public String save() throws Throwable {
		LOG.info("################################");
		if (organization.getId() == null) {
			LOG.info("Sem ID");
			LOG.info(organization.toString());
			service.create(organization);
		} else {
			LOG.info("ID " + organization.getId());
			LOG.info(organization.toString());
			service.update(organization);
		}
		FacesMessage info = new FacesMessage("Registro salvo com sucesso!!");
        facesContext.addMessage(null, info);
		return "index.xhtml?faces-redirect=true";
	}
	
	public List<Organization> listAll() throws DatabaseException {
		return service.findAll();
	}

}
