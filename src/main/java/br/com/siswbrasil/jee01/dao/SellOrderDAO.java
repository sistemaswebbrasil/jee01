package br.com.siswbrasil.jee01.dao;

import javax.ejb.Stateless;
import br.com.siswbrasil.jee01.model.SellOrder;
import br.com.siswbrasil.jee01.model.SellOrderPK;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class SellOrderDAO extends GenericDAO<SellOrder,SellOrderPK> {

	@PersistenceContext(unitName = "default")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}	

	public SellOrderDAO() {
		super(SellOrder.class);
	}	
}
