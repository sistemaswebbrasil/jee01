package br.com.siswbrasil.jee01.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import br.com.siswbrasil.jee01.dao.GenericDAO;
import br.com.siswbrasil.jee01.exception.CustomExceptions;
import br.com.siswbrasil.jee01.exception.DatabaseException;

public abstract class GenericServiceImpl<T extends Serializable> implements GenericService<T> {

	@Inject
	protected GenericDAO<T> dao;

	@Override
	public List<T> findAll()  {
		return getDao().findAll();
//		try {
//			return dao.findAll();
//		} catch (Exception e) {
//			throw new DatabaseException("Falha ao executar a operação no banco de dados", e);
//		}
	}

	@Override
	public void create(T entity) throws Throwable {
		try {
			getDao().create(entity);
		} catch (Exception e) {			
			throw CustomExceptions.converterException(e);
		}
	}

	@Override
	public void update(T entity) throws DatabaseException {
		try {
			getDao().update(entity);
		} catch (Exception e) {
			throw new DatabaseException("Falha ao executar a operação no banco de dados", e);
		}
	}

	/*
	 * 
	 * // public void update(User user) { // facade.edit(user); // }
	 * 
	 * 
	 * 
	 * public void edit(T entity) { getEntityManager().merge(entity); }
	 * 
	 * 
	 */

	@Override
	public T findById(long id) {
		return getDao().find(id);
	}

	@Override
	public void delete(T entity) {
		getDao().remove(entity);
	}

	@Override
	public void deleteById(long id) {
		getDao().remove(this.findById(id));
	}

	@Override
	public long count() {
		return getDao().count();
	}

	public GenericDAO<T> getDao() {
		return dao;
	}

	public void setDao(GenericDAO<T> dao) {
		this.dao = dao;
	}
	
//	private Throwable converterException(Throwable throwable) {
//
//		Throwable rootCause = com.google.common.base.Throwables.getRootCause(throwable);
//		System.out.println("1#############################################");
//		System.out.println(rootCause);
//		System.out.println("1#############################################");
//		if (rootCause instanceof SQLException) {
//			
//			String sqlErrorMessage = rootCause.getMessage();
//			
//			System.out.println("2#############################################");
//			System.out.println(((SQLException) rootCause).getSQLState());
//			System.out.println("2#############################################");			
//			
//			if ("23505".equals(((SQLException) rootCause).getSQLState())) {
//				
//				System.out.println("3#############################################");
//				System.out.println(((SQLException) rootCause).getSQLState());
//				System.out.println("3#############################################");
//				System.out.println(((SQLException) rootCause).getErrorCode() );
//				System.out.println("3#############################################");
//				System.out.println(((SQLException) rootCause).getLocalizedMessage() );
//				System.out.println("3#############################################");
//				System.out.println(((SQLException) rootCause).getMessage() );
//				System.out.println("3#############################################");
//				//System.out.println(((SQLException) rootCause).getCause().getMessage() );
//				System.out.println("3#############################################");	
//				String[] lines = sqlErrorMessage.split("\\n");
//
//				return new DatabaseException(MessageUtil.getMsg("error.sql.generic"),
//						MessageUtil.getMsg("error.sql.uniqueViolation") + "."+lines[1], throwable);
//			} else {
//				System.out.println("4#############################################");
//				System.out.println(((SQLException) rootCause).getSQLState());
//				System.out.println("4#############################################");				
//
//				return new DatabaseException(MessageUtil.getMsg("error.sql.generic"),
//						MessageUtil.getMsg("error.sql.unknown.state"), throwable);
//			}
//		}
//
//		return throwable;
//	}	

}
