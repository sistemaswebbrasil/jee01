package br.com.siswbrasil.jee01.dao;

import javax.ejb.Stateless;
import br.com.siswbrasil.jee01.model.User;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class UserDAO extends GenericDAO<User, Long> {

	@PersistenceContext(unitName = "default")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public UserDAO() {
		super(User.class);
	}

	public User findByEmail(String email) {
		try {		
			Query query = getEntityManager().createQuery("SELECT u FROM User u WHERE u.email=:email");			
			return (User) query.setParameter("email", email).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
