package br.com.siswbrasil.jee01.facade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.siswbrasil.jee01.model.User;

@Stateless
public class UserFacade extends AbstractFacade<User> {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserFacade() {
        super(User.class);
    }
    
}
