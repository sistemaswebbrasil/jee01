package br.com.siswbrasil.jee01.service;

import java.util.List;

import br.com.siswbrasil.jee01.exception.DatabaseException;

public interface GenericService<T,ID> {

	List<T> findAll() throws DatabaseException;

	void create(T entity) throws Throwable;
	
	void update(T entity) throws Throwable;

	T findById(ID id);

	void delete(T entity);

	void deleteById(ID id);

	long count();	

}
