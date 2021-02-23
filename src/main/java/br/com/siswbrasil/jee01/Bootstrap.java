package br.com.siswbrasil.jee01;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import br.com.siswbrasil.jee01.exception.BusinessException;
import br.com.siswbrasil.jee01.model.Organization;
import br.com.siswbrasil.jee01.model.Product;
import br.com.siswbrasil.jee01.model.Role;
import br.com.siswbrasil.jee01.model.Task;
import br.com.siswbrasil.jee01.model.User;
import br.com.siswbrasil.jee01.service.OrganizationService;
import br.com.siswbrasil.jee01.service.ProductService;
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
	
	@Inject
	private ProductService productService;

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
				initialTasks();
				initOrganizations();
				initRoles();
				initialUsers();
				initialProducts();
			} catch (BusinessException e) {
				LOG.log(Level.SEVERE,
						"Falha ao gerar os dados iniciais de desenvolvimento , aparentemente erro nos dados enviados para o servidor",
						e);
			} catch (Throwable e) {
				LOG.log(Level.SEVERE, "Falha ao gerar os dados iniciais de desenvolvimento ", e);
			}
		}
	}
	
	private void initialProducts() throws Throwable {
		Product product1 = new Product(null,"Arroz",new BigDecimal(25.50));
		Product product2 = new Product(null,"Feijão",new BigDecimal(9.50));
		Product product3 = new Product(null,"Macarrão",new BigDecimal(5.50));
		Product product4 = new Product(null,"Óleo",new BigDecimal(9.0));
		Product product5 = new Product(null,"Azeite",new BigDecimal(22.99));
		Product product6 = new Product(null,"Linguiça",new BigDecimal(15.20));
		Product product7 = new Product(null,"Biscoito",new BigDecimal(3.50));
		Product product8 = new Product(null,"Pão de Forma",new BigDecimal(6.20));
		Product product9 = new Product(null,"Cerveja Lata",new BigDecimal(3.50));
		Product product10 = new Product(null,"Água 500ml",new BigDecimal(1.50));
		
		Product product11 = new Product(null,"Massa de Tomate",new BigDecimal(5.50));
		Product product12 = new Product(null,"Saco de Lixo",new BigDecimal(12.0));
		Product product13 = new Product(null,"Café",new BigDecimal(14.00));
		Product product14 = new Product(null,"Sabonete",new BigDecimal(2.50));
		Product product15 = new Product(null,"Escova de dente",new BigDecimal(15.99));
		Product product16 = new Product(null,"Pasta de dente",new BigDecimal(3.00));
		Product product17 = new Product(null,"Espuma de barbear",new BigDecimal(22.99));
		Product product18 = new Product(null,"Vodka",new BigDecimal(28.90));
		Product product19 = new Product(null,"Salsicha",new BigDecimal(4.00));
		Product product20 = new Product(null,"Camarão",new BigDecimal(35.20));		
		Product product21 = new Product(null,"Desodorante",new BigDecimal(11.50));
		
		productService.create(product1);
		productService.create(product2);
		productService.create(product3);
		productService.create(product4);
		productService.create(product5);
		productService.create(product6);
		productService.create(product7);
		productService.create(product8);
		productService.create(product9);
		productService.create(product10);
		productService.create(product11);
		productService.create(product12);
		productService.create(product13);
		productService.create(product14);
		productService.create(product15);
		productService.create(product16);
		productService.create(product17);
		productService.create(product18);
		productService.create(product19);
		productService.create(product20);
		productService.create(product21);
	}

	private void initialUsers() throws Throwable, BusinessException {
		LOG.log(Level.INFO, "Initials user list");
		
		Organization organization1 = organizationService.findById(1L);
		Organization organization2 = organizationService.findById(2L);
		Organization organization3 = organizationService.findById(3L);
		
		User user1 = new User(null, "Adriano Faria Alves", "adriano.faria@gmail.com", "adriano.faria",null,organization1);
		User user2 = new User(null, "Michele Cristina Teixeira Faria Alves", "micheletalves@gmail.com", "mixxa19",null,organization2);
		User user3 = new User(null, "Beatriz Teixeira Faria Alves", "beatriz.t.f.alves@gmail.com", "bia",null,organization3);	
		
		Role role1 = roleService.findById(1L);
		Role role2 = roleService.findById(2L);
		Role role3 = roleService.findById(3L);
		
		List<Role> roleList1 = new ArrayList<Role>();
		List<Role> roleList2 = new ArrayList<Role>();
		List<Role> roleList3 = new ArrayList<Role>();
		
		roleList1.add(role1);
		roleList1.add(role2);
		roleList1.add(role3);
		
		roleList2.add(role2);
		roleList2.add(role1);
		
		roleList3.add(role3);
		
		user1.setRoleList(roleList1);
		user2.setRoleList(roleList2);
		user3.setRoleList(roleList3);
		
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
