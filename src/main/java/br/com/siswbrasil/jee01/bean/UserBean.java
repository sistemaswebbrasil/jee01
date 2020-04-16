package br.com.siswbrasil.jee01.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.siswbrasil.jee01.model.User;
import br.com.siswbrasil.jee01.service.UserService;
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

	private Long id;

	private String name;

	private User user;

	public String save() {
		User user = new User();
		user.setName(name);
		service.create(user);

		id = null;
		name = null;

		return "index.xhtml?faces-redirect=true";
	}

	public List<User> listAll() {
		return service.findAll();
	}

	public String edit(Long id) {
		UserBean editRecord = new UserBean();

		User user = service.findById(id);

		editRecord.setId(user.getId());
		editRecord.setName(user.getName());

		Map<String, Object> sessionMapObj = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

		sessionMapObj.put("editRecordObj", editRecord);

		return "edit.xhtml?faces-redirect=true";
	}

	public String update(UserBean userBean) {
		User user = new User();
		
		user.setId(userBean.getId());
		user.setName(userBean.getName());		
		
		service.update(user);
		return "index.xhtml?faces-redirect=true";
	}

	public void delete(Long id) {
		service.delete(id);
	}

}
