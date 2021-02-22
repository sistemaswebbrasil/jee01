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

import br.com.siswbrasil.jee01.exception.ScaffoldException;
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
	public static final String SCAFFOLD_DAO_PATH = "scaffold.dao.path";
	public static final String SCAFFOLD_GENERATE_MODELS_PATH = "scaffold.generate_models.path";
	public static final String SCAFFOLD_SERVICE_PATH = "scaffold.service.path";
	public static final String SCAFFOLD_BEAN_PATH = "scaffold.bean.path";
	public static final String SCAFFOLD_DATA_MODEL_PATH = "scaffold.data.model.path";
	public static final String SCAFFOLD_VIEW_PATH = "scaffold.view.path";
	public static final String SCAFFOLD_MENU_FILE_PATH = "scaffold.menu_file.path";
	public static final String SCAFFOLD_MENU_MARK_ITEM = "scaffold.menu_mark_item.value";

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

		String auxType = "aux";
		String labelType = "label";
		String packageType = "package";
		String pathType = "path";
		String classNameType = "class";
		String entityPackage = "";
		String idType = "";
		String idName = "";

		for (String line : lines) {
			objectContent += line + "\n";

			String selectedName = null;
			String selectedType = null;
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
					addNewProperty(selected, properties, "field", selectedType, selectedName, selectedIsId, null);
					if (selectedIsId == true) {
						idType = selectedType;
						idName = selectedName;
					}
					selectedIsId = false;
				}
			}

			if (line.startsWith("package")) {
				String packageName = line.split("package ")[1].trim();
				selectedName = "packageName";
				selectedType = "package";
				entityPackage = packageName;// String.format("%s.%s", packageName,selected.getName());
				addNewProperty(selected, properties, "package", null, "packageName", false, packageName);
			}
		}

		String entityCamelCase = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, selected.getName());
		String entityListCamelCase = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, selected.getName()) + "List";
		String entityCreateCamelCase = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, selected.getName()) + "Create";
		String entityEditCamelCase = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, selected.getName()) + "Edit";
		String entityDetailCamelCase = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, selected.getName()) + "Detail";
		String entityDeleteCamelCase = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, selected.getName()) + "Delete";
		String basePath = propertiesUtil.get(SCAFFOLD_BASE_PATH);
		String daoParcialPath = propertiesUtil.get(SCAFFOLD_DAO_PATH);
		String modelParcialDaoPath = propertiesUtil.get(SCAFFOLD_GENERATE_MODELS_PATH);
		String daoPath = basePath.concat(daoParcialPath);
		String modelDaoPath = basePath.concat(modelParcialDaoPath.concat("/dao.txt"));
		String daoPackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".dao");
		String servicePackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".service");
		String daoClass = selected.getName().concat("DAO");
		String serviceClass = selected.getName().concat("Service");
		String daoClassPath = String.format("%s/%s.%s", daoPath, daoClass, "java");
		String serviceParcialPath = propertiesUtil.get(SCAFFOLD_SERVICE_PATH);
		String modelParcialServicePath = propertiesUtil.get(SCAFFOLD_GENERATE_MODELS_PATH);
		String servicePath = basePath.concat(serviceParcialPath);
		String serviceClassPath = String.format("%s/%s.%s", servicePath, serviceClass, "java");
		String modelServicePath = basePath.concat(modelParcialServicePath.concat("/service.txt"));
		String utilPackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".util");
		String beanParcialPath = propertiesUtil.get(SCAFFOLD_BEAN_PATH);
		String modelParcialBeanPath = propertiesUtil.get(SCAFFOLD_GENERATE_MODELS_PATH);
		String beanPath = basePath.concat(beanParcialPath);
		String modelBeanPath = basePath.concat(modelParcialBeanPath.concat("/bean.txt"));
		String modelBeanLazyPath = basePath.concat(modelParcialBeanPath.concat("/beanLazy.txt"));
		String beanPackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".bean");
		String exceptionPackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".exception");
		String beanClass = selected.getName().concat("Bean");
		String beanClassPath = String.format("%s/%s.%s", beanPath, beanClass, "java");
		String beanClassView = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, beanClass);
		String entityIdGet = "get" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, idName);
		String dataModelParcialPath = propertiesUtil.get(SCAFFOLD_DATA_MODEL_PATH);
		String dataModelPath = basePath.concat(dataModelParcialPath);
		String modelDataModelPath = basePath.concat(modelParcialBeanPath.concat("/dataModel.txt"));
		String dataModelPackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".datamodel");
		String dataModelClass = selected.getName().concat("DataModel");
		String dataModelClassPath = String.format("%s/%s.%s", dataModelPath, dataModelClass, "java");

		String baseViewPath = propertiesUtil.get(SCAFFOLD_VIEW_PATH);
		String baseViewFolderName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, selected.getName());
		String baseViewFolderPath = baseViewPath + "/" + baseViewFolderName;
		String baseViewFormPath = baseViewFolderPath + "/form.xhtml";
		String modelViewFormPath = basePath.concat(modelParcialBeanPath.concat("/viewForm.txt"));
		String entityCreateLabel = entityCamelCase + ".entityCreate";

		String entityListLabel = entityCamelCase + ".entityList";
		String entityEditLabel = entityCamelCase + ".entityEdit";
		String entityDetailLabel = entityCamelCase + ".entityDetail";
		String entityDeleteLabel = entityCamelCase + ".entityDelete";

		String baseViewListPath = baseViewFolderPath + "/index.xhtml";
		String modelViewListPath = basePath.concat(modelParcialBeanPath.concat("/index.txt"));
		String modelViewListLazyPath = basePath.concat(modelParcialBeanPath.concat("/indexLazy.txt"));

		String markerMenu = propertiesUtil.get(SCAFFOLD_MENU_MARK_ITEM) ;
		String modelItemMenuPath = basePath.concat(modelParcialBeanPath.concat("/itemMenu.txt"));
		String menuItemPath = propertiesUtil.get(SCAFFOLD_MENU_FILE_PATH);

		addNewProperty(selected, properties, labelType, null, "entityCamelCase", false, entityCamelCase);
		addNewProperty(selected, properties, labelType, null, "entityList", false, entityListCamelCase);
		addNewProperty(selected, properties, labelType, null, "entityCreate", false, entityCreateCamelCase);
		addNewProperty(selected, properties, labelType, null, "entityEdit", false, entityEditCamelCase);
		addNewProperty(selected, properties, labelType, null, "entityDetail", false, entityDetailCamelCase);
		addNewProperty(selected, properties, labelType, null, "entityDeleteCamelCase", false, entityDeleteCamelCase);
		
		addNewProperty(selected, properties, labelType, null, "entityCreateLabel", false, entityCreateLabel);
		addNewProperty(selected, properties, labelType, null, "entityListLabel", false, entityListLabel);
		addNewProperty(selected, properties, labelType, null, "entityEditLabel", false, entityEditLabel);
		addNewProperty(selected, properties, labelType, null, "entityDetailLabel", false, entityDetailLabel);
		addNewProperty(selected, properties, labelType, null, "entityDeleteLabel", false, entityDeleteLabel);

		addNewProperty(selected, properties, packageType, null, "entityPackage", false, entityPackage);
		addNewProperty(selected, properties, packageType, null, "servicePackage", false, servicePackage);
		addNewProperty(selected, properties, packageType, null, "daoPackage", false, daoPackage);
		addNewProperty(selected, properties, packageType, null, "utilPackage", false, utilPackage);
		addNewProperty(selected, properties, packageType, null, "beanPackage", false, beanPackage);
		addNewProperty(selected, properties, packageType, null, "exceptionPackage", false, exceptionPackage);
		addNewProperty(selected, properties, packageType, null, "dataModelPackage", false, dataModelPackage);

		addNewProperty(selected, properties, pathType, null, "basePath", false, basePath);
		addNewProperty(selected, properties, pathType, null, "daoParcialPath", false, daoParcialPath);
		addNewProperty(selected, properties, pathType, null, "modelParcialDaoPath", false, modelParcialDaoPath);
		addNewProperty(selected, properties, pathType, null, "modelDaoPath", false, modelDaoPath);
		addNewProperty(selected, properties, pathType, null, "daoClassPath", false, daoClassPath);
		addNewProperty(selected, properties, pathType, null, "serviceClassPath", false, serviceClassPath);
		addNewProperty(selected, properties, pathType, null, "serviceParcialPath", false, serviceParcialPath);
		addNewProperty(selected, properties, pathType, null, "modelParcialServicePath", false, modelParcialServicePath);
		addNewProperty(selected, properties, pathType, null, "servicePath", false, servicePath);
		addNewProperty(selected, properties, pathType, null, "modelServicePath", false, modelServicePath);
		addNewProperty(selected, properties, pathType, null, "daoPath", false, daoPath);
		addNewProperty(selected, properties, pathType, null, "beanClassPath", false, beanClassPath);
		addNewProperty(selected, properties, pathType, null, "beanPath", false, beanPath);
		addNewProperty(selected, properties, pathType, null, "modelBeanPath", false, modelBeanPath);
		addNewProperty(selected, properties, pathType, null, "modelBeanLazyPath", false, modelBeanLazyPath);
		addNewProperty(selected, properties, pathType, null, "dataModelParcialPath", false, dataModelParcialPath);
		addNewProperty(selected, properties, pathType, null, "dataModelPath", false, dataModelPath);
		addNewProperty(selected, properties, pathType, null, "modelDataModelPath", false, modelDataModelPath);
		addNewProperty(selected, properties, pathType, null, "dataModelParcialPath", false, dataModelParcialPath);
		addNewProperty(selected, properties, pathType, null, "baseViewPath", false, baseViewPath);
		addNewProperty(selected, properties, pathType, null, "baseViewFolderName", false, baseViewFolderName);
		addNewProperty(selected, properties, pathType, null, "baseViewFolderPath", false, baseViewFolderPath);
		addNewProperty(selected, properties, pathType, null, "baseViewFormPath", false, baseViewFormPath);
		addNewProperty(selected, properties, pathType, null, "modelViewFormPath", false, modelViewFormPath);
		addNewProperty(selected, properties, pathType, null, "modelItemMenuPath", false, modelItemMenuPath);
		addNewProperty(selected, properties, pathType, null, "menuItemPath", false, menuItemPath);
		addNewProperty(selected, properties, pathType, null, "baseViewListPath", false, baseViewListPath);
		addNewProperty(selected, properties, pathType, null, "modelViewListPath", false, modelViewListPath);
		addNewProperty(selected, properties, pathType, null, "modelViewListLazyPath", false, modelViewListLazyPath);

		addNewProperty(selected, properties, classNameType, null, "daoClass", false, daoClass);
		addNewProperty(selected, properties, classNameType, null, "serviceClass", false, serviceClass);
		addNewProperty(selected, properties, classNameType, null, "beanClass", false, beanClass);
		addNewProperty(selected, properties, classNameType, null, "dataModelClass", false, dataModelClass);
		addNewProperty(selected, properties, classNameType, null, "dataModelClassPath", false, dataModelClassPath);

		addNewProperty(selected, properties, auxType, null, "idType", false, idType);
		addNewProperty(selected, properties, auxType, null, "idName", false, idName);
		addNewProperty(selected, properties, auxType, null, "entityIdGet", false, entityIdGet);
		addNewProperty(selected, properties, auxType, null, "beanClassView", false, beanClassView);
		addNewProperty(selected, properties, auxType, null, "markerMenu", false, markerMenu);

		selected.setProperties(properties);
		return objectContent;
	}

	private void addNewProperty(AvailableObject selected, List<AvaliableProperties> properties, String newType,
			String javaType, String newName, boolean isId, String newValue) {
		AvaliableProperties property = selected.new AvaliableProperties(newType, javaType, newName, isId, newValue);

		properties.add(property);
	}

	public String generateLabels(AvailableObject selected) throws ScaffoldException {
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
			if (!item.getType().contains("label") && !item.getType().contains("field")) {
				continue;
			}

			String formattedName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, item.getName());
			if (!StringUtils.isEmpty(item.getValue())) {
				formattedName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, item.getValue());
			}
			formattedName = formattedName.replace("-", " ");
			formattedName = StringUtils.capitalize(formattedName);
			String newLine = String.format("%s.%s=%s", getPropertyByName("entity", selected).getValue(), item.getName(),
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

		objectContent = buildObjectContent(objectContent, newLines);

		writeInFile(objectContent, labelPath);

		return objectContent;
	}

	public String generateDAO(AvailableObject selected) throws ScaffoldException {
		String objectContent = "";
		String modelDaoPath = getPropertyByName("modelDaoPath", selected).getValue();
		String daoClassPath = getPropertyByName("daoClassPath", selected).getValue();

		objectContent = replaceClassFromTemplate(selected, objectContent, modelDaoPath, daoClassPath);
		return objectContent;
	}

	public String generateService(AvailableObject selected) throws ScaffoldException {
		String objectContent = "";
		String modelServicePath = getPropertyByName("modelServicePath", selected).getValue();
		String serviceClassPath = getPropertyByName("serviceClassPath", selected).getValue();

		objectContent = replaceClassFromTemplate(selected, objectContent, modelServicePath, serviceClassPath);
		return objectContent;
	}

	public String generateBean(AvailableObject selected) throws ScaffoldException {
		String objectContent = "";
		String modelBeanPath = getPropertyByName("modelBeanPath", selected).getValue();
		String beanClassPath = getPropertyByName("beanClassPath", selected).getValue();

		objectContent = replaceClassFromTemplate(selected, objectContent, modelBeanPath, beanClassPath);
		return objectContent;
	}

	public String generateBeanLazy(AvailableObject selected) throws ScaffoldException {
		String objectContent = "";
		String modelBeanLazyPath = getPropertyByName("modelBeanLazyPath", selected).getValue();
		String beanClassPath = getPropertyByName("beanClassPath", selected).getValue();

		objectContent = replaceClassFromTemplate(selected, objectContent, modelBeanLazyPath, beanClassPath);
		return objectContent;
	}

	public String generateDataModel(AvailableObject selected) throws ScaffoldException {

		String objectContent = "";
		String modelDataModelPath = getPropertyByName("modelDataModelPath", selected).getValue();
		String dataModelClassPath = getPropertyByName("dataModelClassPath", selected).getValue();

		objectContent = replaceClassFromTemplate(selected, objectContent, modelDataModelPath, dataModelClassPath);
		return objectContent;
	}

	
	public String generateViewForm(AvailableObject selected) throws ScaffoldException {
		String objectContent = "";

		String modelViewFormPath = getPropertyByName("modelViewFormPath", selected).getValue();
		String baseViewFormPath = getPropertyByName("baseViewFormPath", selected).getValue();
		objectContent = replaceFromTemplate(selected, objectContent, modelViewFormPath);

		int beginFieldsTemplate = objectContent.indexOf("${partial.fields.begin}");
		int endFieldsTemplate = objectContent.indexOf("${partial.fields.end}") + 21;
		String templateFields = objectContent.substring(beginFieldsTemplate, endFieldsTemplate);
		String templateFieldsFinal = templateFields;
		templateFieldsFinal = templateFieldsFinal.replace("${partial.fields.begin}", "");
		templateFieldsFinal = templateFieldsFinal.replace("${partial.fields.end}", "");
		String finalLines = "";

		for (AvaliableProperties item : selected.getProperties()) {

			if (StringUtils.isEmpty(item.getValue())) {
				String tmp = "";
				if (!item.getIsId()) {
					tmp = templateFieldsFinal.replace("${entity.field}", item.getName());
					tmp = tmp.replace("${entity.var}", getPropertyByName("entityCamelCase", selected).getValue());
					tmp = tmp.replace("${view.beanForm}", getPropertyByName("beanClassView", selected).getValue());
					tmp = tmp.replace("${optional.render.begin}", "");
					tmp = tmp.replace("${optional.render.end}", "");
					tmp = tmp.replace("${optional.hidden}", "");
					tmp = tmp.replace("${optional.disable}", "");
				} else {
					tmp = templateFieldsFinal.replace("${entity.field}", item.getName());
					tmp = tmp.replace("${entity.var}", getPropertyByName("entityCamelCase", selected).getValue());
					tmp = tmp.replace("${view.beanForm}", getPropertyByName("beanClassView", selected).getValue());
					tmp = tmp.replace("${optional.render.begin}",
							String.format("<ui:fragment rendered=\"#{not empty %s.%s.%s}\">",
									getPropertyByName("beanClassView", selected).getValue(),
									getPropertyByName("entityCamelCase", selected).getValue(), item.getName()));
					tmp = tmp.replace("${optional.render.end}", "</ui:fragment>");
					tmp = tmp.replace("${optional.hidden}",
							String.format("<h:inputHidden value=\"#{%s.%s.%s}\" />",
									getPropertyByName("beanClassView", selected).getValue(),
									getPropertyByName("entityCamelCase", selected).getValue(), item.getName()));
					tmp = tmp.replace("${optional.disable}", "disabled=\"true\"");
				}
				finalLines += tmp + "\n";
			}
		}
		objectContent = objectContent.replace(templateFields, finalLines);
		writeInFile(objectContent, baseViewFormPath);
		return objectContent;

	}

	public String generateViewList(AvailableObject selected) throws ScaffoldException {
		String objectContent = "";

		String modelViewListPath = getPropertyByName("modelViewListPath", selected).getValue();
		String baseViewListPath = getPropertyByName("baseViewListPath", selected).getValue();
		objectContent = replaceFromTemplate(selected, objectContent, modelViewListPath);

		int beginFieldsTemplate = objectContent.indexOf("${partial.fields.begin}");
		int endFieldsTemplate = objectContent.indexOf("${partial.fields.end}") + 21;

		String templateFields = objectContent.substring(beginFieldsTemplate, endFieldsTemplate);
		String templateFieldsFinal = templateFields;
		templateFieldsFinal = templateFieldsFinal.replace("${partial.fields.begin}", "");
		templateFieldsFinal = templateFieldsFinal.replace("${partial.fields.end}", "");

		String finalLines = "";

		for (AvaliableProperties item : selected.getProperties()) {

			if (StringUtils.isEmpty(item.getValue()) && !item.getIsId()) {

				String tmp = templateFieldsFinal.replace("${entity.field}", item.getName());
				tmp = tmp.replace("${entity.var}", getPropertyByName("entityCamelCase", selected).getValue());
				tmp = tmp.replace("${view.beanList}", getPropertyByName("beanClassView", selected).getValue());

				finalLines += tmp + "\n";
			}
		}
		objectContent = objectContent.replace(templateFields, finalLines);
		writeInFile(objectContent, baseViewListPath);
		return objectContent;
	}

	public String generateViewListLazy(AvailableObject selected) throws ScaffoldException {
		String objectContent = "";

		String modelViewListLazyPath = getPropertyByName("modelViewListLazyPath", selected).getValue();
		String baseViewListPath = getPropertyByName("baseViewListPath", selected).getValue();
		objectContent = replaceFromTemplate(selected, objectContent, modelViewListLazyPath);

		int beginFieldsTemplate = objectContent.indexOf("${partial.fields.begin}");
		int endFieldsTemplate = objectContent.indexOf("${partial.fields.end}") + 21;

		String templateFields = objectContent.substring(beginFieldsTemplate, endFieldsTemplate);
		String templateFieldsFinal = templateFields;
		templateFieldsFinal = templateFieldsFinal.replace("${partial.fields.begin}", "");
		templateFieldsFinal = templateFieldsFinal.replace("${partial.fields.end}", "");
		String finalLines = "";

		for (AvaliableProperties item : selected.getProperties()) {

			if (StringUtils.isEmpty(item.getValue()) && !item.getIsId()) {

				String tmp = templateFieldsFinal.replace("${entity.field}", item.getName());
				tmp = tmp.replace("${entity.var}", getPropertyByName("entityCamelCase", selected).getValue());
				tmp = tmp.replace("${view.beanList}", getPropertyByName("beanClassView", selected).getValue());

				finalLines += tmp + "\n";

			}
		}

		objectContent = objectContent.replace(templateFields, finalLines);
		writeInFile(objectContent, baseViewListPath);
		return objectContent;
	}

	public String generateMenuItem(AvailableObject selected) throws ScaffoldException {
		String objectContent = "";
		String objectContentNewItem = getPropertyByName("markerMenu", selected).getValue() + "\n";

		String modelItemMenuPath = getPropertyByName("modelItemMenuPath", selected).getValue();
		String menuItemPath = getPropertyByName("menuItemPath", selected).getValue();
		objectContentNewItem = replaceFromTemplate(selected, objectContentNewItem, modelItemMenuPath);
		
		
		
		List<String> lines1 = readFile(menuItemPath, false);
		List<String> newLines1 = new ArrayList<String>();
		boolean menuItemExist = false;
		for (String line : lines1) {
			if (line.contains(String.format("<li class=\"nav-item has-treeview\" id=\"item-menu-scaffold-%s\">",
					getPropertyByName("entityCamelCase", selected).getValue() )) == true) {
				menuItemExist = true;
			}
			newLines1.add(line);
		}

		String objectFinalMenuContent = "";
		if (menuItemExist == false) {
			for (String line : newLines1) {
				objectFinalMenuContent += line + "\n";
			}
			objectContent = objectFinalMenuContent.replace(getPropertyByName("markerMenu", selected).getValue(), objectContentNewItem);
		}
		
		writeInFile(objectContent, menuItemPath);
		return objectContent;
	}

	private String replaceClassFromTemplate(AvailableObject selected, String objectContent, String templateFile,
			String finalPath) throws ScaffoldException {
		if (selected.getProperties().isEmpty()) {
			throw new ScaffoldException(MessageUtil.getMsg("error.scaffold.object.not_found"));
		}

		objectContent = replaceFromTemplate(selected, objectContent, templateFile);

		String fileAux = new File(finalPath).getParent();
		File baseViewFolderPathDir = new File(fileAux);
		if (!baseViewFolderPathDir.exists()) {
			baseViewFolderPathDir.mkdirs();
		}

		writeInFile(objectContent, finalPath);
		return objectContent;
	}

	private String replaceFromTemplate(AvailableObject selected, String objectContent, String templateFile) {
		List<String> lines = readFile(templateFile, false);

		List<String> newLines = new ArrayList<String>();
		for (String line : lines) {
			if (line.contains("${dao.package}")) {
				line = line.replace("${dao.package}", getPropertyByName("daoPackage", selected).getValue());
			}
			if (line.contains("${entity.package}")) {
				line = line.replace("${entity.package}", getPropertyByName("entityPackage", selected).getValue());
			}
			if (line.contains("${entity.class}")) {
				line = line.replace("${entity.class}", selected.getName());
			}
			if (line.contains("${entity.id.type}")
					&& StringUtils.isEmpty(getPropertyByName("idType", selected).getValue()) == false) {
				line = line.replace("${entity.id.type}", getPropertyByName("idType", selected).getValue());
			}
			if (line.contains("${dao.class}")) {
				line = line.replace("${dao.class}", getPropertyByName("daoClass", selected).getValue());
			}
			if (line.contains("${service.package}")) {
				line = line.replace("${service.package}", getPropertyByName("servicePackage", selected).getValue());
			}
			if (line.contains("${service.class}")) {
				line = line.replace("${service.class}", getPropertyByName("serviceClass", selected).getValue());
			}
			if (line.contains("${bean.package}")) {
				line = line.replace("${bean.package}", getPropertyByName("beanPackage", selected).getValue());
			}
			if (line.contains("${bean.class}")) {
				line = line.replace("${bean.class}", getPropertyByName("beanClass", selected).getValue());
			}
			if (line.contains("${entity.var}")) {
				line = line.replace("${entity.var}", getPropertyByName("entityCamelCase", selected).getValue());
			}
			if (line.contains("${entity.id.var}")) {
				line = line.replace("${entity.id.var}",
						getPropertyByName("entityCamelCase", selected).getValue() + "Id");
			}
			if (line.contains("${util.package}")) {
				line = line.replace("${util.package}", getPropertyByName("utilPackage", selected).getValue());
			}
			if (line.contains("${exception.package}")) {
				line = line.replace("${exception.package}", getPropertyByName("exceptionPackage", selected).getValue());
			}
			if (line.contains("${entity.id.get}")) {
				line = line.replace("${entity.id.get}", getPropertyByName("entityIdGet", selected).getValue());
			}
			if (line.contains("${datamodel.package}")) {
				line = line.replace("${datamodel.package}", getPropertyByName("dataModelPackage", selected).getValue());
			}
			if (line.contains("${datamodel.class}")) {
				line = line.replace("${datamodel.class}", getPropertyByName("dataModelClass", selected).getValue());
			}
			if (line.contains("${entity.id.field}")) {
				line = line.replace("${entity.id.field}", getPropertyByName("idName", selected).getValue());
			}
			if (line.contains("${view.beanForm}")) {
				line = line.replace("${view.beanForm}", getPropertyByName("beanClassView", selected).getValue());
			}
			if (line.contains("${view.beanList}")) {
				line = line.replace("${view.beanList}", getPropertyByName("beanClassView", selected).getValue());
			}
			if (line.contains("${entity.label.create}")) {
				line = line.replace("${entity.label.create}",
						getPropertyByName("entityCreateLabel", selected).getValue());
			}
			if (line.contains("${entity.label.list}")) {
				line = line.replace("${entity.label.list}", getPropertyByName("entityListLabel", selected).getValue());
			}
			if (line.contains("${entity.label.edit}")) {
				line = line.replace("${entity.label.edit}", getPropertyByName("entityEditLabel", selected).getValue());
			}
			if (line.contains("${entity.label.detail}")) {
				line = line.replace("${entity.label.detail}",
						getPropertyByName("entityDetailLabel", selected).getValue());
			}
			if (line.contains("${entity.label.delete}")) {
				line = line.replace("${entity.label.delete}",
						getPropertyByName("entityDeleteLabel", selected).getValue());
			}
			if (line.contains("${entity.resources}")) {
				line = line.replace("${entity.resources}",
						getPropertyByName("baseViewFolderName", selected).getValue());
			}

			newLines.add(line);
		}
		objectContent = buildObjectContent(objectContent, newLines);
		return objectContent;
	}

	private String buildObjectContent(String objectContent, List<String> newLines) {
		for (String line : newLines) {
			objectContent += line + "\n";
		}
		return objectContent;
	}

	private void writeInFile(String objectContent, String filePath) throws ScaffoldException {
		if (!StringUtils.isNotBlank(objectContent.trim())) {			
			throw new ScaffoldException(MessageUtil.getMsg("error.file.content.empty"));			
		}
		
		String fileAux = new File(filePath).getParent();
		File baseViewFolderPathDir = new File(fileAux);
		if (!baseViewFolderPathDir.exists()) {
			baseViewFolderPathDir.mkdirs();
		}
		try {
			FileWriter fileWriter = new FileWriter(filePath);
			fileWriter.write(objectContent);
			fileWriter.close();
		} catch (IOException e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("error.file.fail_write"));
		}
	}

	private AvaliableProperties getPropertyByName(String property, AvailableObject selected) {
		List<AvaliableProperties> properties = selected.getProperties();

		for (AvaliableProperties item : properties) {
			if (item.getName().equalsIgnoreCase(property)) {
				return item;
			}
		}
		return null;
	}

}
