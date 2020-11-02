package br.com.siswbrasil.jee01.service;

import java.util.List;

import br.com.siswbrasil.jee01.exception.DatabaseException;

public interface GenericService<T> {

	List<T> findAll() throws DatabaseException;

	void create(T entity) throws DatabaseException, Throwable;
	
	void update(T entity) throws DatabaseException;

	T findById(long id);

	void delete(T entity);

	void deleteById(long id);

	long count();	

}
