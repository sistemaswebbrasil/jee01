package br.com.siswbrasil.jee01.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import br.com.siswbrasil.jee01.model.Country;
import br.com.siswbrasil.jee01.service.CountryService;
import br.com.siswbrasil.jee01.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Model
public class CountryBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Country country = new Country();

	@Inject
	private CountryService service;

	@PostConstruct
	public void init() {
		country.setContinent("a");
		country.setIddCode("a");
		country.setName("a");
	}

	public String save() {
		try {
			service.save(country);
			country = new Country();
			MessageUtil.addSuccessMessage("Criado com sucesso");
			return "index.xhtml?faces-redirect=true";
		} catch (Exception e) {			
			MessageUtil.addErrorMessage("Falha",e.getLocalizedMessage());
			return null;
		}
	}

}
