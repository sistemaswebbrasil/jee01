package br.com.siswbrasil.jee01.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.siswbrasil.jee01.dao.UserDAO;
import br.com.siswbrasil.jee01.exception.BusinessException;
import br.com.siswbrasil.jee01.model.User;
import br.com.siswbrasil.jee01.util.MessageUtil;

@Stateless
public class UserService extends GenericServiceImpl<User> {

	@Inject
	protected UserDAO dao;

	@Override
	public void create(User user) throws Throwable {
		if (!dao.isUniqueEmail(user.getEmail())) {
			throw new BusinessException(MessageUtil.getMsg("error.uniqueEmail"),
					MessageUtil.getMsg("error.uniqueEmail.detail"));
		}
		super.create(user);
	}
}
