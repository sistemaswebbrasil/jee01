package br.com.siswbrasil.jee01;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import br.com.siswbrasil.jee01.model.Organization;
import br.com.siswbrasil.jee01.model.Task;
import br.com.siswbrasil.jee01.model.User;
import br.com.siswbrasil.jee01.service.OrganizationService;
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

	@PostConstruct
	public void init() throws Throwable {
		String env = propertiesUtil.get("env");
		LOG.log(Level.INFO, "----------------------------------------");
		LOG.log(Level.INFO, "bootstraping application...");
		LOG.log(Level.INFO, "Profile: " + env);
		LOG.log(Level.INFO, "----------------------------------------");
		
		if (env.equalsIgnoreCase("dev")) {			
			initialUsers();
			initialTasks();
			initOrganizations();
		}
	}

	private void initialUsers() throws Throwable {
		LOG.log(Level.INFO, "Initials user list");
		User user1 = new User(null, "Adriano Faria Alves", "adriano.faria@gmail.com", "adriano.faria");
		User user2 = new User(null, "Michele Cristina Teixeira Faria Alves", "micheletalves@gmail.com", "mixxa19");
		User user3 = new User(null, "Beatriz Teixeira Faria Alves", "beatriz.t.f.alves@gmail.com", "bia");
		userService.create(user1);
		userService.create(user2);
		userService.create(user3);
	}
	
	private void initialTasks() throws Throwable {
		LOG.log(Level.INFO, "Initials task list");
		Task task1 = new Task(null, "Estudar", "Reserva um pouco do tempo de folga para se atualizar.");
		Task task2 = new Task(null, "Trabalhar", "Trabalhar um pouco para pagar as contas.");
		Task task3 = new Task(null, "Dormir", "Dormir para descançar a cabeça.");
		taskService.create(task1);
		taskService.create(task2);
		taskService.create(task3);		
	}
	
	private void initOrganizations() throws Throwable {
		Organization organization = new Organization(null, "Home", "Casa Minha", "");
		organizationService.create(organization);
	}
}
