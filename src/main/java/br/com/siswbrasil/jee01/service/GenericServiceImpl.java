package br.com.siswbrasil.jee01.service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import br.com.siswbrasil.jee01.dao.GenericDAO;
import br.com.siswbrasil.jee01.exception.CustomExceptions;

public abstract class GenericServiceImpl<T,ID extends Serializable> implements GenericService<T,ID> {

	@Inject
	protected GenericDAO<T,ID> dao;

	@Override
	public List<T> findAll()  {
		return getDao().findAll();
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
	public void update(T entity) throws Throwable,Exception,SQLException {
		try {
			getDao().update(entity);
			System.out.println("--------------------------------------------------");
			System.out.println(entity);
		} catch (Exception e) {			
			//throw CustomExceptions.converterException(e);
			throw new Exception("Erro", e);
		}
	}

	@Override
	public T findById(ID id) {
		return getDao().find(id);
	}

	@Override
	public void delete(T entity) {
		getDao().remove(entity);
	}

	@Override
	public void deleteById(ID id) {
		getDao().remove(this.findById(id));
	}

	@Override
	public long count() {
		return getDao().count();
	}

	public GenericDAO<T,ID> getDao() {
		return dao;
	}

	public void setDao(GenericDAO<T,ID> dao) {
		this.dao = dao;
	}

}
