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
import java.util.logging.Level;
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
					addNewProperty(selected, properties, "field",selectedType, selectedName, selectedIsId,null);
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
				entityPackage = packageName;//String.format("%s.%s", packageName,selected.getName());				
				addNewProperty(selected, properties, "package",null, "packageName",false, packageName);
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
		String entityIdGet = "get"+CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, idName);		
		String dataModelParcialPath = propertiesUtil.get(SCAFFOLD_DATA_MODEL_PATH);		
		String dataModelPath = basePath.concat(dataModelParcialPath);
		String modelDataModelPath = basePath.concat(modelParcialBeanPath.concat("/dataModel.txt"));
		String dataModelPackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".datamodel");		
		String dataModelClass = selected.getName().concat("DataModel");		
		String dataModelClassPath = String.format("%s/%s.%s", dataModelPath, dataModelClass, "java");		


		addNewProperty(selected, properties, labelType,null, "entityCamelCase", false,entityCamelCase);
		addNewProperty(selected, properties, labelType,null, "entityList", false,entityListCamelCase);
		addNewProperty(selected, properties, labelType,null, "entityCreate", false,entityCreateCamelCase);
		addNewProperty(selected, properties, labelType,null, "entityEdit", false,entityEditCamelCase);		
		addNewProperty(selected, properties, labelType,null, "entityDetail", false,entityDetailCamelCase);
		addNewProperty(selected, properties, labelType,null, "entityDelete", false,entityDeleteCamelCase);
		
		addNewProperty(selected, properties, packageType,null, "entityPackage", false,entityPackage);
		addNewProperty(selected, properties, packageType,null, "servicePackage", false,servicePackage);		
		addNewProperty(selected, properties, packageType,null, "daoPackage", false,daoPackage);
		addNewProperty(selected, properties, packageType,null, "utilPackage", false,utilPackage);
		addNewProperty(selected, properties, packageType,null, "beanPackage", false,beanPackage);
		addNewProperty(selected, properties, packageType,null, "exceptionPackage", false,exceptionPackage);		
		addNewProperty(selected, properties, packageType,null, "dataModelPackage", false,dataModelPackage);		
		
		addNewProperty(selected, properties, pathType,null, "basePath", false,basePath);
		addNewProperty(selected, properties, pathType,null, "daoParcialPath", false,daoParcialPath);
		addNewProperty(selected, properties, pathType,null, "modelParcialDaoPath", false,modelParcialDaoPath);
		addNewProperty(selected, properties, pathType,null, "modelDaoPath", false,modelDaoPath);
		addNewProperty(selected, properties, pathType,null, "daoClassPath", false,daoClassPath);
		addNewProperty(selected, properties, pathType,null, "serviceClassPath", false,serviceClassPath);		
		addNewProperty(selected, properties, pathType,null, "serviceParcialPath", false,serviceParcialPath);
		addNewProperty(selected, properties, pathType,null, "modelParcialServicePath", false,modelParcialServicePath);
		addNewProperty(selected, properties, pathType,null, "servicePath", false,servicePath);
		addNewProperty(selected, properties, pathType,null, "modelServicePath",false, modelServicePath);
		addNewProperty(selected, properties, pathType,null, "daoPath", false,daoPath);
		addNewProperty(selected, properties, pathType,null, "beanClassPath", false,beanClassPath);		
		addNewProperty(selected, properties, pathType,null, "beanPath", false,beanPath);
		addNewProperty(selected, properties, pathType,null, "modelBeanPath", false,modelBeanPath);
		addNewProperty(selected, properties, pathType,null, "modelBeanLazyPath", false,modelBeanLazyPath);		
		addNewProperty(selected, properties, pathType,null, "dataModelParcialPath", false,dataModelParcialPath);
		addNewProperty(selected, properties, pathType,null, "dataModelPath", false,dataModelPath);
		addNewProperty(selected, properties, pathType,null, "modelDataModelPath", false,modelDataModelPath);		
		addNewProperty(selected, properties, pathType,null, "dataModelParcialPath", false,dataModelParcialPath);
		
		addNewProperty(selected, properties, classNameType,null, "daoClass", false,daoClass);
		addNewProperty(selected, properties, classNameType,null, "serviceClass", false,serviceClass);
		addNewProperty(selected, properties, classNameType,null, "beanClass", false,beanClass);		
		addNewProperty(selected, properties, classNameType,null, "dataModelClass", false,dataModelClass);		
		addNewProperty(selected, properties, classNameType,null, "dataModelClassPath", false,dataModelClassPath);
		
		addNewProperty(selected, properties, auxType,null, "idType", false,idType);
		addNewProperty(selected, properties, auxType,null, "idName", false,idName);
		addNewProperty(selected, properties, auxType,null, "entityIdGet", false,entityIdGet);
		
		selected.setProperties(properties);
		return objectContent;
	}

	private void addNewProperty(AvailableObject selected, List<AvaliableProperties> properties, String newType,String javaType,
			String newName,boolean isId, String newValue) {
		AvaliableProperties property = selected.new AvaliableProperties(newType,javaType, newName, isId, newValue);

		properties.add(property);
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
			if (!item.getType().contains("label") && !item.getType().contains("field") ) {
				continue;
			}

			String formattedName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, item.getName());
			if (!StringUtils.isEmpty(item.getValue())) {
				formattedName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, item.getValue());
			}
			formattedName = formattedName.replace("-", " ");
			formattedName = StringUtils.capitalize(formattedName);
			String newLine = String.format("%s.%s=%s", getPropertyByName("entity", selected).getValue(), item.getName(),formattedName);

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
		
		objectContent = replaceFromTemplate(selected, objectContent, modelDaoPath, daoClassPath);
		return objectContent;
	}
	
	public String generateService(AvailableObject selected) throws ScaffoldException {
		String objectContent = "";
		String modelServicePath = getPropertyByName("modelServicePath", selected).getValue();
		String serviceClassPath = getPropertyByName("serviceClassPath", selected).getValue();
		
		objectContent = replaceFromTemplate(selected, objectContent, modelServicePath, serviceClassPath);
		return objectContent;
	}	
	
	public String generateBean(AvailableObject selected) throws ScaffoldException {
		String objectContent = "";
		String modelBeanPath = getPropertyByName("modelBeanPath", selected).getValue();
		String beanClassPath = getPropertyByName("beanClassPath", selected).getValue();
		
		objectContent = replaceFromTemplate(selected, objectContent, modelBeanPath, beanClassPath);
		return objectContent;
	}
	
	public String generateBeanLazy(AvailableObject selected) throws ScaffoldException {
		String objectContent = "";
		String modelBeanLazyPath = getPropertyByName("modelBeanLazyPath", selected).getValue();
		String beanClassPath = getPropertyByName("beanClassPath", selected).getValue();
		
		objectContent = replaceFromTemplate(selected, objectContent, modelBeanLazyPath, beanClassPath);
		return objectContent;
	}
	
	public String generateDataModel(AvailableObject selected) throws ScaffoldException {
		
		String objectContent = "";
		String modelDataModelPath = getPropertyByName("modelDataModelPath", selected).getValue();
		String dataModelClassPath = getPropertyByName("dataModelClassPath", selected).getValue();
		
		objectContent = replaceFromTemplate(selected, objectContent, modelDataModelPath, dataModelClassPath);
		return objectContent;		
		
//		try {
//			if (selected.getProperties().isEmpty()) {
//				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"),
//						MessageUtil.getMsg("error.scaffold.not_found"));
//			}
//			String basePath = propertiesUtil.get(SCAFFOLD_BASE_PATH);
//			String dataModelParcialPath = propertiesUtil.get(SCAFFOLD_DATA_MODEL_PATH);
//			String modelParcialBeanPath = propertiesUtil.get(SCAFFOLD_GENERATE_MODELS_PATH);
//			String dataModelPath = basePath.concat(dataModelParcialPath);
//			String modelDataModelPath = basePath.concat(modelParcialBeanPath.concat("/dataModel.txt"));
//			String entityPackage = "";
//			String idType = "";
//			String entityCamelCase = "";
//			for (AvaliableProperties entityLine : selected.getProperties()) {
//				if (entityLine.getName() == "packageName") {
//					entityPackage = entityLine.getValue();
//				}
//				if (entityLine.getIsId().booleanValue() == true) {
//					idType = entityLine.getType();
//				}
//				if (entityLine.getType() == "entityCamelCase") {
//					entityCamelCase = entityLine.getValue();
//				}
//				System.out.println("------------------------------");
//				System.out.println(entityLine);
//
//			}
//			String beanPackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".bean");
//			String dataModelPackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".datamodel");
//			String utilPackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".util");
//			String exceptionPackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".exception");
//			String dataModelClass = selected.getName().concat("DataModel");
//			String daoPackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".dao");
//			String daoClass = selected.getName().concat("DAO");
//			String dataModelClassPath = String.format("%s/%s.%s", dataModelPath, dataModelClass, "java");
//
//			List<String> lines = scaffoldService.readFile(modelDataModelPath, false);
//			List<String> newLines = new ArrayList<String>();
//
//			for (String line : lines) {
//				if (line.contains("${bean.package}")) {
//					line = line.replace("${bean.package}", beanPackage);
//				}
//				if (line.contains("${entity.package}")) {
//					line = line.replace("${entity.package}", String.format("%s.%s", entityPackage, selected.getName()));
//				}
//				if (line.contains("${entity.class}")) {
//					line = line.replace("${entity.class}", selected.getName());
//				}
//				if (line.contains("${datamodel.package}")) {
//					line = line.replace("${datamodel.package}", dataModelPackage);
//				}
//				if (line.contains("${datamodel.class}")) {
//					line = line.replace("${datamodel.class}", dataModelClass);
//				}
//				if (line.contains("${dao.class}")) {
//					line = line.replace("${dao.class}", daoClass);
//				}
//				if (line.contains("${dao.package}")) {
//					line = line.replace("${dao.package}", String.format("%s.%s", daoPackage, daoClass));
//				}
//				if (line.contains("${entity.var}")) {
//					line = line.replace("${entity.var}", entityCamelCase);
//				}
//				if (line.contains("${entity.id.var}")) {
//					line = line.replace("${entity.id.var}", entityCamelCase + "Id");
//				}
//				if (line.contains("${entity.id.type}")) {
//					line = line.replace("${entity.id.type}", idType);
//				}
//				if (line.contains("${util.package}")) {
//					line = line.replace("${util.package}", utilPackage);
//				}
//				if (line.contains("${exception.package}")) {
//					line = line.replace("${exception.package}", exceptionPackage);
//				}
//				newLines.add(line);
//			}
//
//			objectContent = "";
//			for (String line : newLines) {
//				objectContent += line + "\n";
//			}
//			LOG.log(Level.INFO, "Gerando o DataModel ...");
//			LOG.log(Level.INFO, "dataModelClassPath " + dataModelClassPath);
//			LOG.log(Level.INFO, "objectContent " + objectContent);
//			LOG.log(Level.INFO, "dataModelPath " + dataModelPath);
//
//			File baseViewFolderPathDir = new File(dataModelPath);
//			if (!baseViewFolderPathDir.exists()) {
//				baseViewFolderPathDir.mkdirs();
//			}
//
//			try {
//				FileWriter fileWriter = new FileWriter(dataModelClassPath);
//				fileWriter.write(objectContent);
//				fileWriter.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("error.file.fail_write"));
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			MessageUtil.addErrorMessage("Falha", "Erro ao gerar o Scaffold para classe tipo Bean");
//		}
	}	

	public String replaceFromTemplate(AvailableObject selected, String objectContent, String templateFile,
			String finalPath) throws ScaffoldException {
		if (selected.getProperties().isEmpty()) {
			throw new ScaffoldException(MessageUtil.getMsg("error.scaffold.object.not_found"));
		}

		List<String> lines = readFile(templateFile, false);
		
		List<String> newLines = new ArrayList<String>();
		for (String line : lines) {
			if (line.contains("${dao.package}")) {
				line = line.replace("${dao.package}", getPropertyByName("daoPackage", selected).getValue());  
			}
			if (line.contains("${entity.package}")) {
				line = line.replace("${entity.package}",getPropertyByName("entityPackage", selected).getValue());
			}
			if (line.contains("${entity.class}")) {
				line = line.replace("${entity.class}", selected.getName());
			}
			if (line.contains("${entity.id.type}") && StringUtils.isEmpty( getPropertyByName("idType", selected).getValue()  ) == false) {
				line = line.replace("${entity.id.type}", getPropertyByName("idType", selected).getValue());
			}
			if (line.contains("${dao.class}")) {
				line = line.replace("${dao.class}", getPropertyByName("daoClass", selected).getValue());
			}			
			if (line.contains("${service.package}")) {
				line = line.replace("${service.package}", getPropertyByName("servicePackage", selected).getValue() );
			}			
			if (line.contains("${service.class}")) {
				line = line.replace("${service.class}", getPropertyByName("serviceClass", selected).getValue() );
			}			
			if (line.contains("${bean.package}")) {
				line = line.replace("${bean.package}", getPropertyByName("beanPackage", selected).getValue() );
			}
			if (line.contains("${bean.class}")) {
				line = line.replace("${bean.class}", getPropertyByName("beanClass", selected).getValue() );
			}
			if (line.contains("${entity.var}")) {
				line = line.replace("${entity.var}", getPropertyByName("entityCamelCase", selected).getValue() );
			}
			if (line.contains("${entity.id.var}")) {
				line = line.replace("${entity.id.var}", getPropertyByName("entityCamelCase", selected).getValue()  + "Id");
			}
			if (line.contains("${util.package}")) {
				line = line.replace("${util.package}", getPropertyByName("utilPackage", selected).getValue() );
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
			newLines.add(line);
		}		
		objectContent = buildObjectContent(objectContent, newLines);
		String fileAux = new File(finalPath).getParent();
		File baseViewFolderPathDir = new File(fileAux);
		if (!baseViewFolderPathDir.exists()) {
			baseViewFolderPathDir.mkdirs();
		}		
		
		
		writeInFile(objectContent, finalPath );
		return objectContent;
	}

	private String buildObjectContent(String objectContent, List<String> newLines) {
		for (String line : newLines) {
			objectContent += line + "\n";
		}
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
