package br.com.siswbrasil.jee01.model.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import br.com.siswbrasil.jee01.model.User;

public class UserRepositoryImpl implements UserRepository {

	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
	private EntityManager entityManager = entityManagerFactory.createEntityManager();

	@Override
	public void create(User entity) {

		entityManager.getTransaction().begin();
		entityManager.persist(entity);
		entityManager.getTransaction().commit();

		entityManager.close();
		entityManagerFactory.close();
	}

	@Override
	public void update(User entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public User findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(User entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<User> findAll() {
		// TODO Auto-generated method stub
		return null;
	}
}
