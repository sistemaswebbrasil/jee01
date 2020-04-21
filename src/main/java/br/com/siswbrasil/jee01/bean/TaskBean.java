package br.com.siswbrasil.jee01.bean;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import br.com.siswbrasil.jee01.facade.TaskFacade;
import br.com.siswbrasil.jee01.model.Task;
import br.com.siswbrasil.jee01.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Named
@RequestScoped
public class TaskBean implements Serializable {

	private static final long serialVersionUID = -2190180383973531783L;

	@EJB
	private TaskFacade facade;
	private List<Task> taskList = null;
	private Task selected = new Task();

	public List<Task> listAll() {
		return facade.findAll();
	}

	public String edit(Long id) {
		selected = facade.find(id);
		return "edit";
	}

	public String update() {
		try {
			facade.edit(selected);
			MessageUtil.addSuccessMessage("Atualizado com sucesso");
			return "index.xhtml?faces-redirect=true";
		} catch (Exception e) {
			MessageUtil.addErrorMessage(e.getMessage());
			return null;
		}
	}

	public String create() {
		try {
			facade.create(selected);
			MessageUtil.addSuccessMessage("Criado com sucesso");

			selected = new Task();
			return "index.xhtml?faces-redirect=true";
		} catch (Exception e) {
			MessageUtil.addErrorMessage(e.getMessage());
			return null;
		}
	}
	
	public String destroy() {
		Task entity = facade.find(selected);
		facade.remove(entity);
		return "index";
	}	

}
