package br.com.siswbrasil.jee01.dao;

import javax.ejb.Stateless;
import br.com.siswbrasil.jee01.model.Address;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class AddressDAO extends GenericDAO<Address,Long> {

	@PersistenceContext(unitName = "default")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}	

	public AddressDAO() {
		super(Address.class);
	}	
}
