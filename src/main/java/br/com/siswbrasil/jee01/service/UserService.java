package br.com.siswbrasil.jee01.service;

import javax.ejb.Stateless;

import br.com.siswbrasil.jee01.model.User;

@Stateless
public class UserService extends GenericServiceImpl<User> {
	//public class UserDAO extends GenericDAO<User> {

	
//@EJB  
//PRIVATE USERFACADE FACADE;
//PRIVATE USERDAO DAO;	
	

//	public List<User> findAll() {
//		return dao.findAll();
//	}	
//	
//	
////	@EJB  
//////	private UserFacade facade;
////	private UserDAO facade;
////	
//	@Transactional
//	public void create(User user) throws DatabaseException {
//		try {
//			dao.create(user);
//		} catch (Exception e) {
//			throw new DatabaseException("Falha ao executar a operação no banco de dados", e);  			
//		}		
//	}
//
//	public List<User> findAll() {
//		return super.getDao().findAll();
//				//dao.findAll();
//				//facade.findAll();
//	}
//
//	public User findById(Long id) {
//		return facade.find(id);
//	}
//
//	public void update(User user) {
//		facade.edit(user);
//	}
//
//	public void delete(Long id) {
//		facade.remove(findById(id));
//	}

}
