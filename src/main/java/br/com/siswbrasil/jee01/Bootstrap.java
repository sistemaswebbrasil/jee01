package br.com.siswbrasil.jee01;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import br.com.siswbrasil.jee01.exception.BusinessException;
import br.com.siswbrasil.jee01.model.Organization;
import br.com.siswbrasil.jee01.model.Role;
import br.com.siswbrasil.jee01.model.Task;
import br.com.siswbrasil.jee01.model.User;
import br.com.siswbrasil.jee01.service.OrganizationService;
import br.com.siswbrasil.jee01.service.RoleService;
import br.com.siswbrasil.jee01.service.TaskService;
import br.com.siswbrasil.jee01.service.UserService;
import br.com.siswbrasil.jee01.util.PropertiesUtil;

@Startup
@Singleton
public class Bootstrap {

	@Inject
	private Logger LOG;

	@Inject
	private UserService userService;

	@Inject
	private TaskService taskService;

	@Inject
	private PropertiesUtil propertiesUtil;

	@Inject
	private OrganizationService organizationService;
	
	@Inject
	private RoleService roleService;

	@PostConstruct
	public void init() {
		String env = propertiesUtil.get("env");
		LOG.log(Level.INFO, "----------------------------------------");
		LOG.log(Level.INFO, "Iniciando a aplicação");
		LOG.log(Level.INFO, "Profile: " + env);
		LOG.log(Level.INFO, "----------------------------------------");

		if (env.equalsIgnoreCase("dev")) {
			LOG.log(Level.INFO, "Gerando dados iniciais de desenvolvimento ...");
			try {
				initialUsers();
				initialTasks();
				initOrganizations();
				initRoles();
			} catch (BusinessException e) {
				LOG.log(Level.SEVERE,
						"Falha ao gerar os dados iniciais de desenvolvimento , aparentemente erro nos dados enviados para o servidor",
						e);
			} catch (Throwable e) {
				LOG.log(Level.SEVERE, "Falha ao gerar os dados iniciais de desenvolvimento ", e);
			}
		}
	}

	private void initialUsers() throws Throwable, BusinessException {
		LOG.log(Level.INFO, "Initials user list");
		User user1 = new User(null, "Adriano Faria Alves", "adriano.faria@gmail.com", "adriano.faria");
		User user2 = new User(null, "Michele Cristina Teixeira Faria Alves", "micheletalves@gmail.com", "mixxa19");
		User user3 = new User(null, "Beatriz Teixeira Faria Alves", "beatriz.t.f.alves@gmail.com", "bia");		
		
		userService.create(user1);
		userService.create(user2);
		userService.create(user3);
	}

	private void initialTasks() throws Throwable, BusinessException {

		LOG.log(Level.INFO, "Initials task list");
		Task task1 = new Task(null, "Estudar", "Reserva um pouco do tempo de folga para se atualizar.");
		Task task2 = new Task(null, "Trabalhar", "Trabalhar um pouco para pagar as contas.");
		Task task3 = new Task(null, "Dormir", "Dormir para descançar a cabeça.");
		taskService.create(task1);
		taskService.create(task2);
		taskService.create(task3);
	}

	private void initOrganizations() throws Throwable, BusinessException {
		Organization organization1 = new Organization(null, "MATRIZ", "Loja Matriz", "");
		Organization organization2 = new Organization(null, "FILIAL 01", "Loja filial Centro", "");
		Organization organization3 = new Organization(null, "FILIAL 02", "Loja filial Galeão", "");
		organizationService.create(organization1);
		organizationService.create(organization2);
		organizationService.create(organization3);
	}
	
	private void initRoles() throws Throwable, BusinessException {
		Role role1 = new Role(null,"ADMIN","Administrador");
		Role role2 = new Role(null,"MANAGER","Gerente");
		Role role3 = new Role(null,"USER","Usuário Comum");
		
		roleService.create(role1);
		roleService.create(role2);
		roleService.create(role3);		
	}
}
