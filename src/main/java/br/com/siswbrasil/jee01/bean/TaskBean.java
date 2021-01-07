package br.com.siswbrasil.jee01.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import javax.annotation.PostConstruct;

import br.com.siswbrasil.jee01.exception.DatabaseException;
import br.com.siswbrasil.jee01.util.MessageUtil;
import br.com.siswbrasil.jee01.model.Task;
import br.com.siswbrasil.jee01.service.TaskService;


@Getter
@Setter
@Named
@RequestScoped
public class TaskBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private TaskService service;
	
    @Inject
    private FacesContext facesContext;
    	
	private Long taskId;
	private Task task;
    
    @PostConstruct
    public void init() throws IOException {    
        if (taskId == null) {
            task = new Task();
        } else {
			task = service.findById(taskId);
			if (task == null) {
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("register_not_found"));
				FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
			}			
        }
    }
    
	public List<Task> listAll() throws DatabaseException {
		return service.findAll();
	}    
    
    public String save() throws Throwable {
    	if (task.getId() == null) {
            service.create(task);
        } else {
            service.update(task);
        }
        
        MessageUtil.addSuccessMessage(MessageUtil.getMsg("success"), MessageUtil.getMsg("create_success"));        
        return "index.xhtml?faces-redirect=true";
    }
    
	public void delete(Long taskId) {
		try {
			service.deleteById(taskId);
			MessageUtil.addSuccessMessage(MessageUtil.getMsg("success"), MessageUtil.getMsg("delete_success"));
		} catch (Exception e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("delete_fail"));							
		}
	}

}
