package br.com.siswbrasil.jee01.dao;

import javax.ejb.Stateless;
import br.com.siswbrasil.jee01.model.Task;

@Stateless
public class TaskDAO extends GenericDAO<Task> {

	public TaskDAO() {
		super(Task.class);
	}	
	
}
