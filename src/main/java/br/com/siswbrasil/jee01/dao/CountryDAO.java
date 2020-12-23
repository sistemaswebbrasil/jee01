package br.com.siswbrasil.jee01.dao;

import javax.ejb.Stateless;
import br.com.siswbrasil.jee01.model.Country;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class CountryDAO extends GenericDAO<Country,String> {

	@PersistenceContext(unitName = "default")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}	

	public CountryDAO() {
		super(Country.class);
	}	
}
