package br.com.siswbrasil.jee01.datamodel;

import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import br.com.siswbrasil.jee01.dao.UserDAO;
import br.com.siswbrasil.jee01.model.User;

@ViewScoped
public class UserDataModel extends LazyDataModel<User> {

	private static final long serialVersionUID = 1L;

	@Inject
	private UserDAO dao;

	@Override
	public List<User> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
		try {
			
			System.out.println("first "+first);
			System.out.println("pageSize "+pageSize);
			System.out.println("sortBy "+sortBy);
			System.out.println("filterBy "+filterBy);
			
			List<User> list = dao.primeFacesFilter(first, pageSize, sortBy, filterBy);
			int rowCount = dao.primeFacesFilterCount(filterBy);
			super.setRowCount(rowCount);
			return list;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
