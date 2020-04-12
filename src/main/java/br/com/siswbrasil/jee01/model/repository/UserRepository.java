package br.com.siswbrasil.jee01.model.repository;

import java.util.List;

import br.com.siswbrasil.jee01.model.User;

public interface UserRepository {

	public void create(User entity);

	public void update(User entity);

	public User findById(Long id);

	public void delete(User entity);

	public List<User> findAll();

}
