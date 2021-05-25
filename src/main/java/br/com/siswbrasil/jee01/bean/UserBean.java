package br.com.siswbrasil.jee01.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import br.com.siswbrasil.jee01.datamodel.UserDataModel;
import br.com.siswbrasil.jee01.exception.DatabaseException;
import br.com.siswbrasil.jee01.model.Organization;
import br.com.siswbrasil.jee01.model.Role;
import br.com.siswbrasil.jee01.model.User;
import br.com.siswbrasil.jee01.service.OrganizationService;
import br.com.siswbrasil.jee01.service.RoleService;
import br.com.siswbrasil.jee01.service.UserService;
import br.com.siswbrasil.jee01.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Named
@ViewScoped
public class UserBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private UserService service;

	@Inject
	private FacesContext facesContext;

	@Inject
	private UserDataModel dataModel;

	@Inject
	private RoleService roleService;

	@Inject
	private OrganizationService organizationService;

	private Long userId;
	private User user;
	private List<Role> availableRoles = new ArrayList<Role>();
	private List<Organization> availablesOrganization = new ArrayList<Organization>();

	private String filterName;

	@PostConstruct
	@Transactional
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
		availableRoles = roleService.findAll();
		availablesOrganization = organizationService.findAll();
	}

	public List<User> listAll() throws DatabaseException {
		return service.findAll();
	}

	public String save() throws Throwable {
		if (user.getId() == null) {
			if (service.emailUnique(user)) {
				service.create(user);
			} else {
				MessageUtil.addErrorMessage("Email já utilizado",
						"Favor selecionar outro email , pois este já está em uso");
				return null;
			}
		} else {
			if (service.emailUnique(user)) {
				service.update(user);
			} else {
				MessageUtil.addErrorMessage("Email já utilizado",
						"Favor selecionar outro email , pois este já está em uso");
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
