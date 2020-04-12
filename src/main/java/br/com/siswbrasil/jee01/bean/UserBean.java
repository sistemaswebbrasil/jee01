package br.com.siswbrasil.jee01.bean;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import br.com.siswbrasil.jee01.model.User;
import br.com.siswbrasil.jee01.service.UserService;
import lombok.Data;

@Data
@Named
@RequestScoped
public class UserBean implements Serializable {

	private static final long serialVersionUID = 1L;


	private User user;

	public void save() {

		UserService service = new UserService();

		service.create(getUser());

	}

}
