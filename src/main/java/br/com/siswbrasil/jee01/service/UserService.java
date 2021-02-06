package br.com.siswbrasil.jee01.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.siswbrasil.jee01.dao.UserDAO;
import br.com.siswbrasil.jee01.model.User;

@Stateless
public class UserService extends GenericServiceImpl<User, Long> {

	@Inject
	protected UserDAO dao;

	public User findByEmail(String email) {
		return dao.findByEmail(email);
	}

	public boolean emailUnique(User userForm) {
		User user = findByEmail(userForm.getEmail());
		if (user != null) {			
			return user.getId().equals(userForm.getId());
		}
		return true;
	}
	
	public User findById(Long id) {
		return dao.findByIdEger(id);
	}	

}
