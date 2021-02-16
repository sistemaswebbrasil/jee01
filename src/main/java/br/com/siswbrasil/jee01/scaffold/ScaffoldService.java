package br.com.siswbrasil.jee01.scaffold;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CaseFormat;

import br.com.siswbrasil.jee01.scaffold.model.AvailableObject;
import br.com.siswbrasil.jee01.scaffold.model.AvailableObject.AvaliableProperties;
import br.com.siswbrasil.jee01.util.MessageUtil;
import br.com.siswbrasil.jee01.util.PropertiesUtil;

@Stateless
public class ScaffoldService {

	@Inject
	private Logger LOG;

	@Inject
	private PropertiesUtil propertiesUtil;

	private String finalEntityPath;

	public static final String SCAFFOLD_BASE_PATH = "scaffold.base.path";
	public static final String SCAFFOLD_ENTITY_PATH = "scaffold.entity.path";
	public static final String SCAFFOLD_LABEL_PATH = "scaffold.labels_pt_BR.path";

	@PostConstruct
	public void init() {
		String basePath = propertiesUtil.get(SCAFFOLD_BASE_PATH);
		String entityPath = propertiesUtil.get(SCAFFOLD_ENTITY_PATH);
		finalEntityPath = basePath.concat(entityPath);
	}

	public List<AvailableObject> readEntitysFromProject() {
		return scanFilesInFolder(finalEntityPath, "entity");
	}

	public List<AvailableObject> scanFilesInFolder(String folder, String type) {
		List<AvailableObject> entities = new ArrayList<AvailableObject>();
		File actual = new File(folder);
		if (!actual.exists()) {
			MessageUtil.clearMessages();
			MessageUtil.addErrorMessage(MessageUtil.getMsg("scaffold.scan.fail"), String.format(
					"O diretório %s não existe,favor configurar o seu amiente \"appication.{env}.properties\" de acordo com a sua máquina de desenvolvimento! ",
					folder));
			return null;
		}

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
	
	public List<String> readFile(String objectPath, Boolean clear) {
		List<String> rowsArray = new ArrayList<String>();
		Path path = Paths.get(objectPath);
		try {
			if (clear) {
				Files.lines(path).map(s -> s.trim()).filter(s -> !((String) s).isEmpty())
						.forEach(s -> rowsArray.add(s));
			} else {
				Files.lines(path).forEach(s -> rowsArray.add(s));
			}
		} catch (IOException e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error.file.not_found"), e.getMessage());
		}
		return rowsArray;
	}	
	
	public String readProperties(AvailableObject selected) {
		String objectContent = "";
		List<String> lines = readFile(selected.getPath(), true);
		List<AvaliableProperties> properties = new ArrayList<>();
		Boolean selectedIsId = false;
		
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

			if (line.startsWith("package")) {
				String packageName = line.split("package ")[1].trim();
				selectedName = "packageName";
				selectedType = "package";
				selectedValue = packageName;
			}

			if (selected.getType().equalsIgnoreCase("entity") && selectedType != null) {
				AvaliableProperties property = selected.new AvaliableProperties(selectedType, selectedName,
						selectedIsId.booleanValue(), selectedValue);
				properties.add(property);
				selectedIsId = false;
			}
		}
		
		String entityCamelCase = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, selected.getName());
		AvaliableProperties property = selected.new AvaliableProperties("entityCamelCase", "entity", false,
				entityCamelCase);
		properties.add(property);

		String entityListCamelCase = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, selected.getName()) + "List";
		property = selected.new AvaliableProperties("entityListCamelCase", "entityList", false, entityListCamelCase);
		properties.add(property);

		String entityCreateCamelCase = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, selected.getName()) + "Create";
		property = selected.new AvaliableProperties("entityCreateCamelCase", "entityCreate", false,
				entityCreateCamelCase);
		properties.add(property);

		String entityEditCamelCase = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, selected.getName()) + "Edit";
		property = selected.new AvaliableProperties("entityEditCamelCase", "entityEdit", false, entityEditCamelCase);
		properties.add(property);

		String entityDetailCamelCase = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, selected.getName()) + "Detail";
		property = selected.new AvaliableProperties("entityDetailCamelCase", "entityDetail", false,
				entityDetailCamelCase);
		properties.add(property);

		String entityDeleteCamelCase = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, selected.getName()) + "Delete";
		property = selected.new AvaliableProperties("entityDeleteCamelCase", "entityDelete", false,
				entityDeleteCamelCase);
		properties.add(property);

		selected.setProperties(properties);		
		return objectContent;
	}
	
	
	public String generateLabels(AvailableObject selected) {
		String objectContent = "";
		String labelPath = propertiesUtil.get(SCAFFOLD_LABEL_PATH);
		if (StringUtils.isEmpty(labelPath)) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"),
					MessageUtil.getMsg("scaffold.labels_config.not_found"));
		}

		List<String> lines = readFile(labelPath, true);
		List<String> newLines = lines;
		List<AvaliableProperties> properties = selected.getProperties();		
		
		for (AvaliableProperties item : properties) {
			if (item.getType().contains("package")) {
				continue;
			}

			String formattedName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, item.getName());
			if (!StringUtils.isEmpty(item.getValue())) {
				formattedName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, item.getValue());
			}
			formattedName = formattedName.replace("-", " ");
			formattedName = StringUtils.capitalize(formattedName);
			String newLine = String.format("%s.%s=%s", getPropertyByName("entity",selected).getValue(), item.getName(),
					formattedName);

			Boolean fieldExist = false;
			for (String line : lines) {
				if (line.substring(0, line.indexOf("=")).equalsIgnoreCase(newLine.substring(0, newLine.indexOf("=")))) {
					fieldExist = true;
				}
			}
			if (fieldExist == false) {
				newLines.add(newLine);
			}
		}
		
		newLines.sort((p1, p2) -> p1.compareTo(p2));


		for (String line : newLines) {
			objectContent += line + "\n";
		}
		
		writeInFile(objectContent, labelPath);
		
		return objectContent;
	}

	private void writeInFile(String objectContent, String labelPath) {
		try {
			FileWriter fileWriter = new FileWriter(labelPath);
			fileWriter.write(objectContent);
			fileWriter.close();
		} catch (IOException e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("error.file.fail_write"));
		}
	}
	
	private AvaliableProperties getPropertyByName(String property,AvailableObject selected) {
		List<AvaliableProperties> properties = selected.getProperties();

		for (AvaliableProperties item : properties) {
			if (item.getName().equalsIgnoreCase(property)) {
				return item;
			}
		}
		return null;
	}	
	

}
