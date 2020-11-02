package br.com.siswbrasil.jee01.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.siswbrasil.jee01.model.Country;

@Stateless
public class CountryDAO extends GenericDAO<Country> {

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
