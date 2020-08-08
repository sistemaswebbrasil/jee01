package br.com.siswbrasil.jee01.scaffold.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

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
	public static final String SCAFFOLD_BASE_PATH = "scaffold.base.path";
	public static final String SCAFFOLD_ENTITY_PATH = "scaffold.entity.path";

	private List<AvailableObject> entities = new ArrayList<AvailableObject>();
	private AvailableObject selected = new AvailableObject();
	private String selectedId;


	@Inject
	private PropertiesUtil propertiesUtil;
	private String finalEntityPath;

	@PostConstruct
	public void init() {
		String basePath = propertiesUtil.get(SCAFFOLD_BASE_PATH);
		String entityPath = propertiesUtil.get(SCAFFOLD_ENTITY_PATH);
		finalEntityPath = basePath.concat(entityPath);
		readEntitysFromProject();
	}

	private void readEntitysFromProject() {
		this.entities = scanFiles(finalEntityPath, "entity");
	}
	
	public void carregaPelaId() {
		Optional<AvailableObject> objectItem = entities
				.stream()
				.filter(item -> item.getName().equals(this.selected.getName() ) )
				.filter(item -> item.getType().equals(this.selected.getType() ) )
				.findFirst();
		
		if (objectItem.isPresent())
			selected = objectItem.get(); 
		else {		
			selected= new AvailableObject();
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("error.object.not_found" ));
			
			try {
				FacesContext context = FacesContext.getCurrentInstance();
				ExternalContext    externalContext = context.getExternalContext();
				externalContext.redirect("index.xhtml");
			} catch (IOException e) {		
				e.printStackTrace();
			}			
		}		
		
	}

	private List<AvailableObject> scanFiles(String folder, String type) {

		List<AvailableObject> entities = new ArrayList<AvailableObject>();
		File actual = new File(folder);
		for (File f : actual.listFiles()) {
			AvailableObject object = new AvailableObject();
			object.setId(UUID.randomUUID().toString());
			object.setName(f.getName());
			object.setType(type);
			object.setPath(f.getAbsolutePath());
			entities.add(object);
		}
		return entities;

	}

}
