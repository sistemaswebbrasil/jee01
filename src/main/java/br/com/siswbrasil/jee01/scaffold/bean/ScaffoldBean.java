package br.com.siswbrasil.jee01.scaffold.bean;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CaseFormat;

import br.com.siswbrasil.jee01.scaffold.model.AvailableObject;
import br.com.siswbrasil.jee01.scaffold.model.AvailableObject.AvaliableProperties;
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
	public static final String SCAFFOLD_LABEL_PATH = "scaffold.labels_pt_BR.path";

	private List<AvailableObject> entities = new ArrayList<AvailableObject>();
	private AvailableObject selected = new AvailableObject();
	private String objectContent;

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
		Optional<AvailableObject> objectItem = entities.stream()
				.filter(item -> item.getName().equals(this.selected.getName()))
				.filter(item -> item.getType().equals(this.selected.getType())).findFirst();

		if (objectItem.isPresent()) {
			selected = objectItem.get();
			readProperties();
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

	private List<AvailableObject> scanFiles(String folder, String type) {

		List<AvailableObject> entities = new ArrayList<AvailableObject>();
		File actual = new File(folder);
		for (File f : actual.listFiles()) {
			AvailableObject object = new AvailableObject();
			object.setId(UUID.randomUUID().toString());
			object.setName(f.getName().replace(".java", ""));
			object.setType(type);
			object.setPath(f.getAbsolutePath());
			entities.add(object);
		}
		return entities;

	}

	public void generateLabels() {
		if (selected.getProperties().isEmpty()) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("error.scaffold.not_found"));
		}

		String labelPath = propertiesUtil.get(SCAFFOLD_LABEL_PATH);

		if (labelPath != null && !labelPath.isEmpty()) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"),
					MessageUtil.getMsg("scaffold.labels_config.not_found"));
		}

		List<String> lines = readFile(labelPath);
		List<String> newLines = new ArrayList<String>();
		List<AvaliableProperties> properties = selected.getProperties();

		for (String line : lines) {
			newLines.add(line);
		}

		for (AvaliableProperties item : properties) {
			String formattedName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, item.getName());
			formattedName = formattedName.replace("-", " ");
			formattedName = StringUtils.capitalize(formattedName);
			String newLine = String.format("%s.%s=%s", selected.getName().toLowerCase(), item.getName(), formattedName);
			Boolean fieldExist = false;
			for (String line : lines) {
				if (line.substring(0, line.indexOf("=")).equalsIgnoreCase(newLine.substring(0, newLine.indexOf("=")))) {
					fieldExist = true;
				}
			}
			System.out.println(fieldExist);
			if (fieldExist == false) {
				newLines.add(newLine);
			}
		}
		
		newLines.sort((p1, p2) -> p1.compareTo(p2));

		objectContent = "";
		for (String line : newLines) {
			objectContent += line + "\n";
		}

		try {
			FileWriter fileWriter = new FileWriter(labelPath);
			fileWriter.write(objectContent);
			fileWriter.close();
		} catch (IOException e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("error.file.fail_write"));
		}
	}

	public void readProperties() {
		List<String> lines = readFile(selected.getPath());
		objectContent = "";
		List<AvaliableProperties> properties = new ArrayList<>();
		Boolean selectedIsId = false;
		objectContent = null;
		for (String line : lines) {
			objectContent += line + "\n";
			String selectedName = null;
			String selectedType = null;
			String selectedValue = null;
			if (line.contains(";")) {
				line = line.substring(0, line.indexOf(";"));
			}
			String[] partLine = line.split(" ");
			if (selected.getType().equalsIgnoreCase("entity")) {
				if (partLine[0].contentEquals("@Id")) {
					selectedIsId = true;
				}
				if (partLine[0].contentEquals("private") && !partLine[1].contentEquals("static")) {
					selectedType = partLine[1];
					selectedName = partLine[2];
				}
			}
			if (selected.getType().equalsIgnoreCase("entity") && selectedType != null) {
				AvaliableProperties property = selected.new AvaliableProperties(selectedType, selectedName,
						selectedIsId.booleanValue(), selectedValue);
				properties.add(property);
				selectedIsId = false;
			}
		}
		selected.setProperties(properties);
	}

	private List<String> readFile(String objectPath) {
		List<String> rowsArray = new ArrayList<String>();
		Path path = Paths.get(objectPath);
		try {
			Files.lines(path).map(s -> s.trim()).filter(s -> !((String) s).isEmpty()).forEach(s -> rowsArray.add(s));
		} catch (IOException e) {
			e.printStackTrace();
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("error.file.not_found"));
		}
		return rowsArray;
	}

}