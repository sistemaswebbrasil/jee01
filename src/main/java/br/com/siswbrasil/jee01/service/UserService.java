package br.com.siswbrasil.jee01.service;

import br.com.siswbrasil.jee01.model.User;
import br.com.siswbrasil.jee01.model.repository.UserRepositoryImpl;

public class UserService {
	
	public void create(User entity) {
		UserRepositoryImpl repository = new UserRepositoryImpl();
		
		repository.create(entity);
	}

}
