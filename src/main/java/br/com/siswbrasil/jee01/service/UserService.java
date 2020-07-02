package br.com.siswbrasil.jee01.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.com.siswbrasil.jee01.facade.UserFacade;
import br.com.siswbrasil.jee01.model.User;

@Stateless
public class UserService {
	
	@EJB
	private UserFacade facade;

	public void create(User user) {		
		facade.create(user);
	}

	public List<User> findAll() {
		return facade.findAll();
	}

	public User findById(Long id) {
		return facade.find(id);
	}

	public void update(User user) {
		facade.edit(user);
	}

	public void delete(Long id) {
		facade.remove(findById(id));
	}

}
