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
import br.com.siswbrasil.jee01.model.Country;
import br.com.siswbrasil.jee01.service.CountryService;


@Getter
@Setter
@Named
@RequestScoped
public class CountryBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private CountryService service;
	
    @Inject
    private FacesContext facesContext;
    	
	private String countryId;
	private Country country;
    
    @PostConstruct
    public void init() throws IOException {    
        if (countryId == null) {
            country = new Country();
        } else {
			country = service.findById(countryId);
			if (country == null) {
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("register_not_found"));
				FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
			}			
        }
    }
    
	public List<Country> listAll() throws DatabaseException {
		return service.findAll();
	}    
    
    public String save() throws Throwable {
    	if (country.getIddCode() == null) {
            service.create(country);
        } else {
            service.update(country);
        }
        
        MessageUtil.addSuccessMessage(MessageUtil.getMsg("success"), MessageUtil.getMsg("create_success"));        
        return "index.xhtml?faces-redirect=true";
    }
    
	public void delete(String countryId) {
		try {
			service.deleteById(countryId);
			MessageUtil.addSuccessMessage(MessageUtil.getMsg("success"), MessageUtil.getMsg("delete_success"));
		} catch (Exception e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("delete_fail"));							
		}
	}

}
