package br.com.siswbrasil.jee01.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.siswbrasil.jee01.exception.BusinessException;
import br.com.siswbrasil.jee01.exception.DatabaseException;
import br.com.siswbrasil.jee01.model.User;
import br.com.siswbrasil.jee01.service.UserService;
import br.com.siswbrasil.jee01.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Named
@RequestScoped
public class UserBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private UserService service;

	private User user = new User();
	private List<User> userList = new ArrayList<User>();

	@PostConstruct
	public void init() {
		user.setEmail("teste@teste.com");
		user.setLoginName("teste");
		user.setName("teste");
	}

	public String save() throws Throwable {
		try {
			service.create(user);
			user = new User();
			MessageUtil.addSuccessMessage("Criado com sucesso");
			return "index.xhtml?faces-redirect=true";
		} catch (BusinessException e) {
			MessageUtil.addErrorMessage(e.getMessage(), e.getDetail());
			return null;
		}
	}
	
	public void emailUnique() {
		if (!service.emailUnique(user.getEmail())) {
			MessageUtil.addErrorMessageWhithField("form.email","Email já utilizado","Favor selecionar outro email , pois este já está em uso");			
		}
	}

	public String update(User editUser) throws Throwable {
		try {
			User user = new User();

			user.setId(editUser.getId());
			user.setName(editUser.getName());
			user.setEmail(editUser.getEmail());

			service.update(editUser);

			MessageUtil.addSuccessMessage("Atualizado com sucesso");
			return "index.xhtml?faces-redirect=true";

		} catch (BusinessException e) {
			MessageUtil.addErrorMessage(e.getMessage(), e.getDetail());
			return null;
		}

	}

	public List<User> listAll() throws DatabaseException {
		return service.findAll();
	}

	public String edit(Long id) {
		User editUser = service.findById(id);
		Map<String, Object> sessionMapObj = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		sessionMapObj.put("editRecordObj", editUser);
		return "edit.xhtml?faces-redirect=true";
	}

	public void delete(Long id) {
		try {
			service.deleteById(id);
			this.listAll();
			MessageUtil.addSuccessMessage("Excluído com sucesso");

		} catch (Exception e) {
			MessageUtil.addErrorMessage(e.getMessage());
		}

	}

}
