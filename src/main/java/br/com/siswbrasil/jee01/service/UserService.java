package br.com.siswbrasil.jee01.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;

import br.com.siswbrasil.jee01.exception.DataBaseRuntimeException;
import br.com.siswbrasil.jee01.exception.DatabaseException;
import br.com.siswbrasil.jee01.facade.UserFacade;
import br.com.siswbrasil.jee01.model.User;

@Stateless
public class UserService {
	
	@EJB  
	private UserFacade facade;

	@Transactional
	public void create(User user) throws DatabaseException  {
		try {
			facade.create(user);
		} catch (Exception e) {
			throw new DatabaseException("Falha ao executar a operação no banco de dados", e);			
		}		
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
