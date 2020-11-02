package br.com.siswbrasil.jee01.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.siswbrasil.jee01.dao.GenericDAO;
import br.com.siswbrasil.jee01.exception.DataBaseRuntimeException;


public abstract class GenericServiceImpl<T extends Serializable> implements GenericService<T> {

	
	private GenericDAO<T> dao;

	@Override
	public List<T> findAll() {
		return dao.findAll();
	}

	@Override
	public void create(T entity) {
		try {
			dao.create(entity);
		} catch (Exception e) {
			throw new DataBaseRuntimeException("Falha ao processar requisição no banco de dados", e);
		}
	}

	@Override
	public T findById(long id) {
		return dao.find(id);
	}

	@Override
	public void delete(T entity) {
		dao.remove(entity);
	}

	@Override
	public void deleteById(long id) {
		dao.remove(this.findById(id));
	}

	@Override
	public long count() {
		return dao.count();
	}

}
