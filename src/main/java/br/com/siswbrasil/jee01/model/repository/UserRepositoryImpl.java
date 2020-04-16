package br.com.siswbrasil.jee01.model.repository;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import br.com.siswbrasil.jee01.model.User;

@Stateless
public class UserRepositoryImpl implements UserRepository {

	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
	private EntityManager entityManager = entityManagerFactory.createEntityManager();

	@SuppressWarnings("unchecked")
	@Override
	public List<User> findAll() {
		return entityManager.createQuery("SELECT u from User u").getResultList();
	}

	@Override
	public void create(User entity) {
		entityManager.getTransaction().begin();
		entityManager.persist(entity);
		entityManager.getTransaction().commit();
	}

	@Override
	public User findById(Long id) {
		return entityManager.find(User.class, id);
	}

	@Override
	public void update(User user) {
		entityManager.getTransaction().begin();
		entityManager.merge(user);
		entityManager.getTransaction().commit();
	}

	@Override
	public void delete(Long id) {
		entityManager.getTransaction().begin();
		entityManager.remove(findById(id));
		entityManager.getTransaction().commit();
	}
}
