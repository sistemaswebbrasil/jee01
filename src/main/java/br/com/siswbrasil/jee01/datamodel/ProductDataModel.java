package br.com.siswbrasil.jee01.datamodel;

import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import br.com.siswbrasil.jee01.dao.ProductDAO;
import br.com.siswbrasil.jee01.model.Product;

@ViewScoped
public class ProductDataModel extends LazyDataModel<Product> {

	private static final long serialVersionUID = 1L;

	@Inject
	private ProductDAO dao;

	@Override
	public List<Product> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
		try {
			List<Product> list = dao.primeFacesFilter(first, pageSize, sortBy, filterBy);
			int rowCount = dao.primeFacesFilterCount(filterBy);
			super.setRowCount(rowCount);
			return list;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
