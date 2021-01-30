package br.com.siswbrasil.jee01.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.Metamodel;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.MatchMode;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

public abstract class GenericDAO<T, ID> {

	protected Class<T> entityClass;
	
	@Inject
	private Logger LOG;

	public GenericDAO(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	
	

	protected abstract EntityManager getEntityManager();

	public void create(T entity) {
		getEntityManager().persist(entity);
	}

	public void update(T entity) {
		LOG.info("Antes de atualizar");
		getEntityManager().merge(entity);
		LOG.info(entity.toString());
		LOG.info("Depois de atualizar");
		
	}

	public void remove(T entity) {
		getEntityManager().remove(getEntityManager().merge(entity));
	}

	public T find(Object id) {
		return getEntityManager().find(entityClass, id);
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		@SuppressWarnings("rawtypes")
		CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		cq.select(cq.from(entityClass));
		return getEntityManager().createQuery(cq).getResultList();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> findRange(int[] range) {
		CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		cq.select(cq.from(entityClass));
		javax.persistence.Query q = getEntityManager().createQuery(cq);
		q.setMaxResults(range[1] - range[0] + 1);
		q.setFirstResult(range[0]);
		return q.getResultList();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int count() {
		CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
		cq.select(getEntityManager().getCriteriaBuilder().count(rt));
		javax.persistence.Query q = getEntityManager().createQuery(cq);
		return ((Long) q.getSingleResult()).intValue();
	}

	public List<T> primeFacesFilter(int first, int pageSize, Map<String, SortMeta> sortBy,
			Map<String, FilterMeta> filterBy) {
		try {

			CriteriaQuery<T> query = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
			Root<T> root = query.from(entityClass);
			ArrayList<Predicate> predicates = new ArrayList<Predicate>();
			CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();

			addFilters(filterBy, query, root, predicates, builder, getEntityManager());
			addOrdinations(sortBy, query, root, builder);

			List<T> lista = getEntityManager().createQuery(query).setFirstResult(first).setMaxResults(pageSize)
					.getResultList();

			return lista;

		} catch (Exception e) {
			e.printStackTrace();

			throw new RuntimeException("Não sei porque está dando erro");
		}

	}

	public int primeFacesFilterCount(Map<String, FilterMeta> filterBy) {

		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<T> root = query.from(entityClass);
		ArrayList<Predicate> predicates = new ArrayList<Predicate>();
		addFilters(filterBy, query, root, predicates, builder, getEntityManager());
		query.multiselect(builder.count(root));
		Long result = getEntityManager().createQuery(query).getSingleResult();

		return result.intValue();

	}

	private void addOrdinations(Map<String, SortMeta> sortBy, CriteriaQuery<T> query, Root<T> root,
			CriteriaBuilder builder) {
		List<Order> orders = new ArrayList<Order>();
		if (sortBy != null && !sortBy.isEmpty()) {

			for (SortMeta meta : sortBy.values()) {
				String sortField = meta.getSortField();
				SortOrder sortOrder = meta.getSortOrder();

				if (sortField.contains(".time")) {
					sortField = sortField.split(".time")[0];
				}

				switch (sortOrder) {
				case DESCENDING:
					orders.add(builder.desc(root.get(sortField)));
					break;
				default:
					orders.add(builder.asc(root.get(sortField)));
					break;
				}
			}
			query.orderBy(orders);
		}
	}

	@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
	private void addFilters(Map<String, FilterMeta> filterBy, CriteriaQuery<?> query, Root<?> root,
			ArrayList<Predicate> predicates, CriteriaBuilder builder, EntityManager em) {
		if (filterBy != null) {
			for (FilterMeta meta : filterBy.values()) {

				String filterField = meta.getFilterField();
				Object filterValue = meta.getFilterValue();
				MatchMode filterMatchMode = meta.getFilterMatchMode();

				if (filterValue != null) {
					if (filterField.contains(".time")) {
						filterField = filterField.split(".time")[0];
					}

					switch (filterMatchMode) {
					case CONTAINS:
						predicates.add(builder.like(root.<String>get(filterField), "%" + filterValue + "%"));
						break;
					case ENDS_WITH:
						predicates.add(builder.like(root.<String>get(filterField), "%" + filterValue));
						break;
					case EQUALS:
						predicates.add(builder.equal(root.<String>get(filterField), filterValue.toString()));
						break;
					case EXACT:
						predicates.add(builder.like(root.<String>get(filterField), filterValue.toString()));
						break;
					case GREATER_THAN:
						predicates.add(builder.greaterThan(root.<String>get(filterField), filterValue.toString()));
						break;
					case GREATER_THAN_EQUALS:
						predicates.add(
								builder.greaterThanOrEqualTo(root.<String>get(filterField), filterValue.toString()));
						break;
					case IN:
						predicates.add(builder.in(root.<String>get(filterField).in(filterValue)));
						break;
					case LESS_THAN:
						predicates.add(builder.lessThan(root.<String>get(filterField), filterValue.toString()));
						break;
					case LESS_THAN_EQUALS:
						predicates.add(builder.lessThanOrEqualTo(root.get(filterField), (Comparable) filterValue));
						break;
					case STARTS_WITH:

						Class tipo = getTypeFromEntity(em, entityClass, filterField);

						if (tipo != null && tipo.getName() == "double") {
							predicates.add(builder.lessThanOrEqualTo(root.get(filterField), (Comparable) filterValue));
							break;
						}
						predicates.add(builder.like(root.<String>get(filterField), filterValue + "%"));
						break;
					default:
						predicates.add(builder.equal(root.<String>get(filterField), filterValue.toString()));
						break;
					}
				}
			}
			query.where(builder.and(predicates.toArray(new Predicate[0])));
		}
	}

	@SuppressWarnings("rawtypes")
	private Class getTypeFromEntity(EntityManager em, Class<T> clazz, String field) {
		Metamodel m = em.getMetamodel();
		IdentifiableType<T> of = (IdentifiableType<T>) m.managedType(clazz);
		Class type = of.getAttribute(field).getJavaType();
		return type;
	}

}
