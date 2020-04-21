package br.com.siswbrasil.jee01.facade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.siswbrasil.jee01.model.Task;

@Stateless
public class TaskFacade extends AbstractFacade<Task> {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TaskFacade() {
        super(Task.class);
    }
    
}
