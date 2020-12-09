package br.com.siswbrasil.jee01.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.siswbrasil.jee01.model.Task;

@Stateless
public class TaskDAO extends GenericDAO<Task,Long> {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TaskDAO() {
        super(Task.class);
    }

}
