package br.com.siswbrasil.jee01.service;

import java.util.List;

import javax.ejb.Stateful;
import javax.inject.Inject;

import br.com.siswbrasil.jee01.dao.GenericDAO;
import br.com.siswbrasil.jee01.exception.DataBaseException;

@Stateful
public abstract class GenericServiceImpl<T> implements GenericService<T> {

	@Inject
	private GenericDAO<T> dao;

	@Override
	public List<T> findAll() {
		return dao.findAll();
	}

	@Override
	public void save(T entity) {
		try {
			dao.create(entity);
		} catch (DataBaseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
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
