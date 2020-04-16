package br.com.siswbrasil.jee01.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.siswbrasil.jee01.bean.UserBean;
import br.com.siswbrasil.jee01.model.User;
import br.com.siswbrasil.jee01.model.repository.UserRepository;

@Stateless
public class UserService {

	@Inject
	private UserRepository repository;

//	public UserService() {
//		this.repository = new UserRepositoryImpl();
//	}

	public void create(User user) {
		repository.create(user);
	}

	public List<User> findAll() {
		return repository.findAll();
	}

	public User findById(Long id) {
		return repository.findById(id);
	}

	public void update(User user) {
		repository.update(user);
	}

	public void delete(Long id) {
		repository.delete(id);
	}
	public void update(UserBean userBean) {
		System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		System.out.println(userBean);
		System.out.println(userBean);
		
	}
}
