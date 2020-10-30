package br.com.siswbrasil.jee01.service;

import java.util.List;

public interface GenericService<T> {

	List<T> findAll();

	void create(T entity);

	T findById(long id);

	void delete(T entity);

	void deleteById(long id);

	long count();

}
