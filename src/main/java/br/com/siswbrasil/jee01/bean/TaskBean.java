package br.com.siswbrasil.jee01.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.siswbrasil.jee01.facade.TaskFacade;
import br.com.siswbrasil.jee01.model.Task;
import br.com.siswbrasil.jee01.service.TaskService;
import br.com.siswbrasil.jee01.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Named
@RequestScoped
public class TaskBean implements Serializable {

	private static final long serialVersionUID = -2190180383973531783L;

	@Inject
	private TaskService service;
	
	private List<Task> taskList = new ArrayList<Task>();
	
	private Task selected = new Task();  

	public List<Task> listAll() {
		return service.findAll();
	}

	public String edit(Long id) {
		selected = service.findById(id);
		return "edit";
	}

	public String update() throws Throwable {
		try {
			service.update(selected);
			MessageUtil.addSuccessMessage("Atualizado com sucesso");
			return "index.xhtml?faces-redirect=true";
		} catch (Exception e) {
			MessageUtil.addErrorMessage(e.getMessage());
			return null;
		}
	}

	public String create() throws Throwable {
		try {
			service.create(selected);
			MessageUtil.addSuccessMessage("Sucesso","Criado com sucesso");

			selected = new Task();
			return "index.xhtml?faces-redirect=true";
		} catch (Exception e) {
			MessageUtil.addErrorMessage(e.getMessage());
			return null;
		}
	}
	
	public void delete(Long id) {
		service.deleteById(id);
		MessageUtil.addSuccessMessage("Sucesso","Removido com sucesso");		
	}	

}
