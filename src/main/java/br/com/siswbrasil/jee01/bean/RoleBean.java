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
import br.com.siswbrasil.jee01.model.Role;
import br.com.siswbrasil.jee01.service.RoleService;


@Getter
@Setter
@Named
@RequestScoped
public class RoleBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private RoleService service;
	
    @Inject
    private FacesContext facesContext;
    	
	private Long roleId;
	private Role role;
    
    @PostConstruct
    public void init() throws IOException {    
        if (roleId == null) {
            role = new Role();
        } else {
			role = service.findById(roleId);
			if (role == null) {
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("register_not_found"));
				FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
			}			
        }
    }
    
	public List<Role> listAll() throws DatabaseException {
		return service.findAll();
	}    
    
    public String save() throws Throwable {
    	if (role.getId() == null) {
            service.create(role);
        } else {
            service.update(role);
        }
        
        MessageUtil.addSuccessMessage(MessageUtil.getMsg("success"), MessageUtil.getMsg("create_success"));        
        return "index.xhtml?faces-redirect=true";
    }
    
	public void delete(Long roleId) {
		try {
			service.deleteById(roleId);
			MessageUtil.addSuccessMessage(MessageUtil.getMsg("success"), MessageUtil.getMsg("delete_success"));
		} catch (Exception e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("delete_fail"));							
		}
	}

}
