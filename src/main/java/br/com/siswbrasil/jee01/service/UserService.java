package br.com.siswbrasil.jee01.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.siswbrasil.jee01.dao.UserDAO;
import br.com.siswbrasil.jee01.model.User;

@Stateless
public class UserService extends GenericServiceImpl<User,Long> {
	
	@Inject
	protected UserDAO dao;	

	public boolean emailUnique(String email) {
		return dao.isUniqueEmail(email);
	}

}
