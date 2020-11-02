package br.com.siswbrasil.jee01.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.siswbrasil.jee01.model.User;

@Stateless
public class UserDAO extends GenericDAO<User> {

	@PersistenceContext(unitName = "default")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public UserDAO() {
		super(User.class);
	}

}
