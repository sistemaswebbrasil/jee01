package br.com.siswbrasil.jee01.dao;

import javax.ejb.Stateless;
import br.com.siswbrasil.jee01.model.User;

@Stateless
public class UserDAO extends GenericDAO<User> {

	public UserDAO() {
		super(User.class);
	}	
	
}
