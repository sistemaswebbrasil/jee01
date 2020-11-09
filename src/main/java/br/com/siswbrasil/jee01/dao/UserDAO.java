package br.com.siswbrasil.jee01.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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

	public boolean isUniqueEmail(String email) {
		try {
			Query query = getEntityManager().createQuery("SELECT u FROM User u WHERE u.email=:email");
			query.setParameter("email", email).getSingleResult();
			return false;
		} catch (NoResultException e) {
			return true;
		}
	}

}
