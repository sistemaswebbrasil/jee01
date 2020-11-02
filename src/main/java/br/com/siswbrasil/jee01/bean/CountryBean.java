package br.com.siswbrasil.jee01.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.siswbrasil.jee01.exception.BusinessException;
import br.com.siswbrasil.jee01.exception.CustomExceptions;
import br.com.siswbrasil.jee01.exception.DatabaseException;
import br.com.siswbrasil.jee01.model.Country;
import br.com.siswbrasil.jee01.service.CountryService;
import br.com.siswbrasil.jee01.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Named
@RequestScoped
public class CountryBean implements Serializable {

	private static final long serialVersionUID = 1L;	

	@Inject
	private CountryService service;
	
	private Country country = new Country();

	@PostConstruct
	public void init() {
		country.setIddCode("a1");
		country.setContinent("a1");		
		country.setName("a1");
	}
	
	public String save() throws Throwable {
		try {
			service.create(country);
			country = new Country();
			MessageUtil.addSuccessMessage("Criado com sucesso");
			return "index.xhtml?faces-redirect=true";
		} catch (Exception e) {			
			BusinessException b = (BusinessException) CustomExceptions.converterException(e);
			MessageUtil.addErrorMessage(b.getMessage(), b.getDetail());
			return null;
		}
	}

}
