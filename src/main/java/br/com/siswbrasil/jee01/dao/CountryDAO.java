package br.com.siswbrasil.jee01.dao;

import javax.ejb.Stateless;
import br.com.siswbrasil.jee01.model.Country;

@Stateless
public class CountryDAO extends GenericDAO<Country> {

	public CountryDAO() {
		super(Country.class);
	}	
	
}
