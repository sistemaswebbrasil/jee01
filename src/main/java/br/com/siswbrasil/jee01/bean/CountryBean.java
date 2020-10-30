package br.com.siswbrasil.jee01.bean;

import java.io.Serializable;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import br.com.siswbrasil.jee01.exception.DataBaseException;
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
		country.setIddCode("a1");
		country.setContinent("a1");		
		country.setName("a1");
	}

	public String save() throws Exception {
		try {
			service.create(country);
			country = new Country();
			MessageUtil.addSuccessMessage("Criado com sucesso");
			return "index.xhtml?faces-redirect=true";
		} catch (DataBaseException e) {
			MessageUtil.addErrorMessage("Falha no banco de dados", e.getCause().getMessage());
			return null;
		}
		catch (EJBTransactionRolledbackException e) {
            Throwable rootCause = com.google.common.base.Throwables.getRootCause(e);
            if (rootCause instanceof SQLException) {            	
                if ("23505".equals(((SQLException) rootCause).getSQLState())) {                	
                	MessageUtil.addErrorMessage(MessageUtil.getMsg("error.sql.generic"), MessageUtil.getMsg("error.sql.uniqueViolation"));
                	return null;
                }else {
                	MessageUtil.addErrorMessage(MessageUtil.getMsg("error.sql.generic"),MessageUtil.getMsg("error.sql.unknown.state") + ((SQLException) rootCause).getSQLState() );                	
                	return null;
                }                	
            }
            MessageUtil.addErrorMessage(MessageUtil.getMsg("error.sql.generic"), MessageUtil.getMsg("error.unknown"));
            return null;            
		}
		catch (RuntimeException e) {			
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error.generic"), e.getCause().getMessage());
			return null;
		}
	}

}
