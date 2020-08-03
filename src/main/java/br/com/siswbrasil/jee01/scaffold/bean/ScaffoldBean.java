package br.com.siswbrasil.jee01.scaffold.bean;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

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

	private List<AvailableObject> scanFiles(String folder, String type) {

		List<AvailableObject> entities = new ArrayList<AvailableObject>();
		File actual = new File(folder);
		for (File f : actual.listFiles()) {
			AvailableObject object = new AvailableObject();
			object.setName(f.getName());
			object.setType(type);
			entities.add(object);
		}
		return entities;

	}

}
