package br.com.siswbrasil.jee01.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.com.siswbrasil.jee01.exception.DataBaseRuntimeException;
import br.com.siswbrasil.jee01.exception.DatabaseException;
import br.com.siswbrasil.jee01.facade.CountryFacade;
import br.com.siswbrasil.jee01.model.Country;

@Stateless
public class CountryService {
	
	@EJB  
	private CountryFacade facade;

	public void create(Country entity) throws DatabaseException {	
		try {
			facade.create(entity);
		} catch (Exception e) {
			//throw new DataBaseRuntimeException("Falha ao executar a operação no banco de dados", e);
			throw new DatabaseException("Falha ao executar a operação no banco de dados", e);
		}
		
	}
	
}
