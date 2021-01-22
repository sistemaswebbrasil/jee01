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
import br.com.siswbrasil.jee01.model.User;
import br.com.siswbrasil.jee01.service.UserService;


@Getter
@Setter
@Named
@RequestScoped
public class UserBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private UserService service;
	
    @Inject
    private FacesContext facesContext;
    	
	private Long userId;
	private User user;
    
    @PostConstruct
    public void init() throws IOException {    
        if (userId == null) {
            user = new User();
        } else {
			user = service.findById(userId);
			if (user == null) {
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("register_not_found"));
				FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
			}			
        }
    }
    
	public List<User> listAll() throws DatabaseException {
		return service.findAll();
	}    
    
    public String save() throws Throwable {
    	if (user.getId() == null) {
			if(service.emailUnique(user)) {
				service.create(user);			    
			}else {
				MessageUtil.addErrorMessage("Email já utilizado","Favor selecionar outro email , pois este já está em uso");
				return null;
			}
        } else {
        	if(service.emailUnique(user)) {
        		service.update(user);
        	}else {
        		MessageUtil.addErrorMessage("Email já utilizado","Favor selecionar outro email , pois este já está em uso");
        		return null;
        	}
        }
    	
    	MessageUtil.addSuccessMessage(MessageUtil.getMsg("success"), MessageUtil.getMsg("create_success"));
    	return "index.xhtml?faces-redirect=true";
    }
    
	public void delete(Long userId) {
		try {
			service.deleteById(userId);
			MessageUtil.addSuccessMessage(MessageUtil.getMsg("success"), MessageUtil.getMsg("delete_success"));
		} catch (Exception e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("delete_fail"));							
		}
	}

}
