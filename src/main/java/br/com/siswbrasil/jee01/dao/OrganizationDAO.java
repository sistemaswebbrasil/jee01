package br.com.siswbrasil.jee01.dao;

import javax.ejb.Stateless;
import br.com.siswbrasil.jee01.model.Organization;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class OrganizationDAO extends GenericDAO<Organization,Long> {

	@PersistenceContext(unitName = "default")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}	

	public OrganizationDAO() {
		super(Organization.class);
	}	
}
