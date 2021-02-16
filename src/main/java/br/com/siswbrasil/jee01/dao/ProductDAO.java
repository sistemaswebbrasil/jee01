package br.com.siswbrasil.jee01.dao;

import javax.ejb.Stateless;
import br.com.siswbrasil.jee01.model.Product;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ProductDAO extends GenericDAO<Product,Long> {

	@PersistenceContext(unitName = "default")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}	

	public ProductDAO() {
		super(Product.class);
	}	
}
