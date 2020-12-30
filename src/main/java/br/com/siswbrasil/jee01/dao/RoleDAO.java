package br.com.siswbrasil.jee01.dao;

import javax.ejb.Stateless;
import br.com.siswbrasil.jee01.model.Role;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class RoleDAO extends GenericDAO<Role,Long> {

	@PersistenceContext(unitName = "default")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}	

	public RoleDAO() {
		super(Role.class);
	}	
}
