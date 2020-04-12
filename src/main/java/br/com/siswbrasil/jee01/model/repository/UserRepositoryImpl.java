package br.com.siswbrasil.jee01.model.repository;

import java.util.List;
import java.util.function.Consumer;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.siswbrasil.jee01.model.User;

public class UserRepositoryImpl implements UserRepository {

	private EntityManager entityManager;

	@Override
	public void create(User entity) {
		executeInsideTransaction(entityManager -> entityManager.persist(entity));

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

	private void executeInsideTransaction(Consumer<EntityManager> action) {
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			action.accept(entityManager);
			tx.commit();
		} catch (RuntimeException e) {
			tx.rollback();
			throw e;
		}
	}

}
