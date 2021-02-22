package br.com.siswbrasil.jee01.scaffold.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.siswbrasil.jee01.exception.ScaffoldException;
import br.com.siswbrasil.jee01.scaffold.ScaffoldService;
import br.com.siswbrasil.jee01.scaffold.model.AvailableObject;
import br.com.siswbrasil.jee01.util.MessageUtil;
import br.com.siswbrasil.jee01.util.PropertiesUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Named
@ViewScoped
public class ScaffoldBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<AvailableObject> entities = new ArrayList<AvailableObject>();
	private AvailableObject selected = new AvailableObject();
	private String objectContent = "";

	@Inject
	private PropertiesUtil propertiesUtil;

	@Inject
	private Logger LOG;

	private String finalEntityPath;

	@Inject
	private ScaffoldService scaffoldService;

	@PostConstruct
	public void init() {
		this.entities = scaffoldService.readEntitysFromProject();
	}

	public void carregaPelaId() {
		Optional<AvailableObject> objectItem = entities.stream()
				.filter(item -> item.getName().equals(this.selected.getName()))
				.filter(item -> item.getType().equals(this.selected.getType())).findFirst();
		if (objectItem.isPresent()) {
			selected = objectItem.get();
			objectContent = scaffoldService.readProperties(selected);
		} else {
			selected = new AvailableObject();
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("error.object.not_found"));
			try {
				FacesContext context = FacesContext.getCurrentInstance();
				ExternalContext externalContext = context.getExternalContext();
				externalContext.redirect("index.xhtml");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void generateLabels() throws ScaffoldException {		
		objectContent = scaffoldService.generateLabels(selected);
	}	

	public void generateDao() {
		try {
			objectContent = scaffoldService.generateDAO(selected);
		} catch (ScaffoldException e) {
			e.printStackTrace();
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), e.getMessage());			
		} 
		catch (Exception e) {
			e.printStackTrace();
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("scaffold.generate_dao.fail"));
		}
	}
	
	public void generateService() {
		try {
			objectContent = scaffoldService.generateService(selected);			
		} catch (ScaffoldException e) {
			e.printStackTrace();
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), e.getMessage());		
		} catch (Exception e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("scaffold.generate_service.fail"));			
		}
	}	

	public void generateBean() {
		try {
			objectContent = scaffoldService.generateBean(selected);			
		} catch (ScaffoldException e) {
			e.printStackTrace();
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), e.getMessage());		
		} catch (Exception e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("scaffold.generate_bean.fail"));			
		}
	}

	public void generateBeanLazy() {
		try {
			objectContent = scaffoldService.generateBeanLazy(selected);			
		} catch (ScaffoldException e) {
			e.printStackTrace();
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), e.getMessage());		
		} catch (Exception e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("scaffold.generate_bean_lazy.fail"));																						 
		}
	}

	public void generateDataModel() {
		try {
			objectContent = scaffoldService.generateDataModel(selected);			
		} catch (ScaffoldException e) {
			e.printStackTrace();
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), e.getMessage());		
		} catch (Exception e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("scaffold.generate_data_model.fail"));																						 
		}
	}

	public void generateViewForm() {
		try {
			objectContent = scaffoldService.generateViewForm(selected);			
		} catch (ScaffoldException e) {
			e.printStackTrace();
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), e.getMessage());		
		} catch (Exception e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("scaffold.generate_view_form.fail"));																						 
		}
	}

	public void generateViewList() {
		try {
			objectContent = scaffoldService.generateViewList(selected);			
		} catch (ScaffoldException e) {
			e.printStackTrace();
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), e.getMessage());		
		} catch (Exception e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("scaffold.generate_view_list.fail"));																						 
		}
	}

	public void generateViewListLazy() {
		try {
			objectContent = scaffoldService.generateViewListLazy(selected);			
		} catch (ScaffoldException e) {
			e.printStackTrace();
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), e.getMessage());		
		} catch (Exception e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("scaffold.generate_view_list_lazy.fail"));																						 
		}
	}

	public void generateMenuItem() {
		try {
			objectContent = scaffoldService.generateMenuItem(selected);			
		} catch (ScaffoldException e) {
			e.printStackTrace();
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), e.getMessage());		
		} catch (Exception e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("scaffold.generate_menu_item.fail"));																						 
		}
	}

}
