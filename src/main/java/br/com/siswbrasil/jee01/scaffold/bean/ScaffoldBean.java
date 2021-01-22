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
import java.util.logging.Logger;

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
	public static final String SCAFFOLD_DAO_PATH = "scaffold.dao.path";
	public static final String SCAFFOLD_SERVICE_PATH = "scaffold.service.path";
	public static final String SCAFFOLD_BEAN_PATH = "scaffold.bean.path";
	public static final String SCAFFOLD_VIEW_PATH = "scaffold.view.path";
	public static final String SCAFFOLD_LABEL_PATH = "scaffold.labels_pt_BR.path";
	public static final String SCAFFOLD_GENERATE_MODELS_PATH = "scaffold.generate_models.path";
	public static final String SCAFFOLD_MENU_FILE_PATH = "scaffold.menu_file.path";
	public static final String SCAFFOLD_MENU_MARK_ITEM = "scaffold.menu_mark_item.value";

	private List<AvailableObject> entities = new ArrayList<AvailableObject>();
	private AvailableObject selected = new AvailableObject();
	private String objectContent;

	@Inject
	private PropertiesUtil propertiesUtil;

	@Inject
	private Logger LOG;

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
		if (!actual.exists()) {
			MessageUtil.clearMessages();
			MessageUtil.addErrorMessage("Falha ao ler diretório", String.format(
					"O diretório %s não existe,favor configurar o seu amiente \"appication.{env}.properties\" de acordo com a sua máquina de desenvolvimento! ",
					folder));
			return null;
		}
		LOG.info("Escaneando os arquivos");
		LOG.info("folder " + folder);
		LOG.info("type " + type);
		LOG.info("entities " + entities);
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

	public void generateService() {
		try {
			if (selected.getProperties().isEmpty()) {
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"),
						MessageUtil.getMsg("error.scaffold.not_found"));
			}
			String basePath = propertiesUtil.get(SCAFFOLD_BASE_PATH);
			String serviceParcialPath = propertiesUtil.get(SCAFFOLD_SERVICE_PATH);
			String modelParcialServicePath = propertiesUtil.get(SCAFFOLD_GENERATE_MODELS_PATH);
			String servicePath = basePath.concat(serviceParcialPath);
			String modelServicePath = basePath.concat(modelParcialServicePath.concat("/service.txt"));
			String entityPackage = "";
			String idType = null;
			for (AvaliableProperties entityLine : selected.getProperties()) {
				if (entityLine.getName() == "packageName") {
					entityPackage = entityLine.getValue();
				}
				if (entityLine.getIsId().booleanValue() == true) {
					idType = entityLine.getType();
				}
			}
			String daoPackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".service");
			String serviceClass = selected.getName().concat("Service");
			String serviceClassPath = String.format("%s/%s.%s", servicePath, serviceClass, "java");
			List<String> lines = readFile(modelServicePath, false);
			List<String> newLines = new ArrayList<String>();
			for (String line : lines) {
				if (line.contains("${service.package}")) {
					line = line.replace("${service.package}", daoPackage);
				}
				if (line.contains("${entity.package}")) {
					line = line.replace("${entity.package}", entityPackage);
				}
				if (line.contains("${entity.class}")) {
					line = line.replace("${entity.class}", selected.getName());
				}
				if (line.contains("${entity.id.type}") && StringUtils.isEmpty(idType) == false) {
					line = line.replace("${entity.id.type}", idType);
				}
				if (line.contains("${service.class}")) {
					line = line.replace("${service.class}", serviceClass);
				}
				newLines.add(line);
			}
			objectContent = "";
			for (String line : newLines) {
				objectContent += line + "\n";
			}
			try {
				FileWriter fileWriter = new FileWriter(serviceClassPath);
				fileWriter.write(objectContent);
				fileWriter.close();
			} catch (IOException e) {
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("error.file.fail_write"));
			}
		} catch (Exception e) {
			MessageUtil.addErrorMessage("Falha", "Erro ao gerar o Scaffold para classe tipo Service");
		}
	}

	public void generateDao() {
		try {

			if (selected.getProperties().isEmpty()) {
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"),
						MessageUtil.getMsg("error.scaffold.not_found"));
			}
			String basePath = propertiesUtil.get(SCAFFOLD_BASE_PATH);
			String daoParcialPath = propertiesUtil.get(SCAFFOLD_DAO_PATH);
			String modelParcialDaoPath = propertiesUtil.get(SCAFFOLD_GENERATE_MODELS_PATH);

			String daoPath = basePath.concat(daoParcialPath);
			String modelDaoPath = basePath.concat(modelParcialDaoPath.concat("/dao.txt"));
			String entityPackage = "";
			String idType = null;
			for (AvaliableProperties entityLine : selected.getProperties()) {
				if (entityLine.getName() == "packageName") {
					entityPackage = entityLine.getValue();
				}
				if (entityLine.getIsId().booleanValue() == true) {
					idType = entityLine.getType();
				}
			}
			String daoPackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".dao");
			String daoClass = selected.getName().concat("DAO");
			String daoClassPath = String.format("%s/%s.%s", daoPath, daoClass, "java");

			List<String> lines = readFile(modelDaoPath, false);
			List<String> newLines = new ArrayList<String>();

			for (String line : lines) {
				if (line.contains("${dao.package}")) {
					line = line.replace("${dao.package}", daoPackage);
				}
				if (line.contains("${entity.package}")) {
					line = line.replace("${entity.package}", entityPackage);
				}
				if (line.contains("${entity.class}")) {
					line = line.replace("${entity.class}", selected.getName());
				}
				if (line.contains("${entity.id.type}") && StringUtils.isEmpty(idType) == false) {
					line = line.replace("${entity.id.type}", idType);
				}
				if (line.contains("${dao.class}")) {
					line = line.replace("${dao.class}", daoClass);
				}
				newLines.add(line);
			}

			objectContent = "";
			for (String line : newLines) {
				objectContent += line + "\n";
			}

			try {
				FileWriter fileWriter = new FileWriter(daoClassPath);
				fileWriter.write(objectContent);
				fileWriter.close();
			} catch (IOException e) {
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("error.file.fail_write"));
			}

		} catch (Exception e) {
			MessageUtil.addErrorMessage("Falha", "Erro ao gerar o Scaffold para classe tipo DAO");
		}

	}

	public void generateBean() {
		try {
			if (selected.getProperties().isEmpty()) {
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"),
						MessageUtil.getMsg("error.scaffold.not_found"));
			}
			String basePath = propertiesUtil.get(SCAFFOLD_BASE_PATH);
			String beanParcialPath = propertiesUtil.get(SCAFFOLD_BEAN_PATH);
			String modelParcialBeanPath = propertiesUtil.get(SCAFFOLD_GENERATE_MODELS_PATH);
			String beanPath = basePath.concat(beanParcialPath);
			String modelBeanPath = basePath.concat(modelParcialBeanPath.concat("/bean.txt"));

			String entityPackage = "";
			String idType = "";
			String entityCamelCase = "";
			for (AvaliableProperties entityLine : selected.getProperties()) {
				if (entityLine.getName() == "packageName") {
					entityPackage = entityLine.getValue();
				}
				if (entityLine.getIsId().booleanValue() == true) {
					idType = entityLine.getType();
				}
				if (entityLine.getType() == "entityCamelCase") {
					entityCamelCase = entityLine.getValue();
				}
				System.out.println("------------------------------");
				System.out.println(entityLine);

			}
			String beanPackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".bean");
			String utilPackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".util");
			String exceptionPackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".exception");
			String beanClass = selected.getName().concat("Bean");
			String servicePackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".service");
			String serviceClass = selected.getName().concat("Service");
			String beanClassPath = String.format("%s/%s.%s", beanPath, beanClass, "java");

			List<String> lines = readFile(modelBeanPath, false);
			List<String> newLines = new ArrayList<String>();

			for (String line : lines) {
				if (line.contains("${bean.package}")) {
					line = line.replace("${bean.package}", beanPackage);
				}
				if (line.contains("${entity.package}")) {
					line = line.replace("${entity.package}", entityPackage);
				}
				if (line.contains("${entity.class}")) {
					line = line.replace("${entity.class}", selected.getName());
				}
				if (line.contains("${entity.id.type}") && StringUtils.isEmpty(idType) == false) {
					line = line.replace("${entity.id.type}", idType);
				}
				if (line.contains("${bean.class}")) {
					line = line.replace("${bean.class}", beanClass);
				}
				if (line.contains("${service.class}")) {
					line = line.replace("${service.class}", serviceClass);
				}
				if (line.contains("${service.package}")) {
					line = line.replace("${service.package}", servicePackage);
				}
				if (line.contains("${entity.var}")) {
					line = line.replace("${entity.var}", entityCamelCase);
				}
				if (line.contains("${entity.id.var}")) {
					line = line.replace("${entity.id.var}", entityCamelCase + "Id");
				}
				if (line.contains("${entity.id.type}")) {
					line = line.replace("${entity.id.type}", idType);
				}
				if (line.contains("${util.package}")) {
					line = line.replace("${util.package}", utilPackage);
				}
				if (line.contains("${exception.package}")) {
					line = line.replace("${exception.package}", exceptionPackage);
				}
				newLines.add(line);
			}

			objectContent = "";
			for (String line : newLines) {
				objectContent += line + "\n";
			}
			System.out.println("Teste1");
			try {
				FileWriter fileWriter = new FileWriter(beanClassPath);
				fileWriter.write(objectContent);
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("error.file.fail_write"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			MessageUtil.addErrorMessage("Falha", "Erro ao gerar o Scaffold para classe tipo Bean");
		}
	}

	public void generateViewForm() {
		try {
			if (selected.getProperties().isEmpty()) {
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"),
						MessageUtil.getMsg("error.scaffold.not_found"));
			}
			String basePath = propertiesUtil.get(SCAFFOLD_BASE_PATH);
			String baseViewPath = propertiesUtil.get(SCAFFOLD_VIEW_PATH);
			// String beanClassPath = String.format("%s/%s.%s", beanPath, beanClass,
			// "java");

			String baseViewFolderName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, selected.getName());

			String baseViewFolderPath = baseViewPath + "/" + baseViewFolderName;
			String baseViewFormPath = baseViewFolderPath + "/form.xhtml";
			String modelParcialBeanPath = propertiesUtil.get(SCAFFOLD_GENERATE_MODELS_PATH);
			// String beanPath = basePath.concat(beanParcialPath);
			String modelViewFormPath = basePath.concat(modelParcialBeanPath.concat("/viewForm.txt"));


			String entityCreateLabel = "";
			String entityListLabel = "";			
			String entityCamelCase = "";
			String entityEditLabel = "";
			String entityDetailLabel = "";
			String entityDeleteLabel = "";
			LOG.info("---------------------DADOS---------------------");
			LOG.info("basePath " + basePath);
			LOG.info("baseViewPath " + baseViewPath);
			LOG.info("baseViewFolderName " + baseViewFolderName);
			LOG.info("baseViewFolderPath " + baseViewFolderPath);
			LOG.info("modelParcialBeanPath " + modelParcialBeanPath);
			LOG.info("modelViewFormPath " + modelViewFormPath);

			for (AvaliableProperties entityLine : selected.getProperties()) {

				if (entityLine.getType() == "entityCamelCase") {
					entityCamelCase = entityLine.getValue();
				}
				if (entityLine.getName() == "entityCreate") {
					entityCreateLabel = entityCamelCase + "." + entityLine.getName();
				}
				if (entityLine.getName() == "entityList") {
					entityListLabel = entityCamelCase + "." + entityLine.getName();
				}
				if (entityLine.getName() == "entityEdit") {
					entityEditLabel = entityCamelCase + "." + entityLine.getName();
				}
				if (entityLine.getName() == "entityDetail") {
					entityDetailLabel = entityCamelCase + "." + entityLine.getName();
				}
				if (entityLine.getName() == "entityDelete") {
					entityDeleteLabel = entityCamelCase + "." + entityLine.getName();
				}

				System.out.println("------------------------------");
				System.out.println(entityLine);

			}
//			String beanPackage = entityPackage.substring(0, entityPackage.lastIndexOf(".")).concat(".bean");
			String beanClass = selected.getName().concat("Bean");
			String beanClassView = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, beanClass);

			List<String> lines = readFile(modelViewFormPath, false);
			List<String> newLines = new ArrayList<String>();

			for (String line : lines) {
				if (line.contains("${view.beanForm}")) {
					line = line.replace("${view.beanForm}", beanClassView);
				}
				if (line.contains("${view.beanForm}")) {
					line = line.replace("${view.beanForm}", beanClassView);
				}
				if (line.contains("${entity.var}")) {
					line = line.replace("${entity.var}", entityCamelCase);
				}
				if (line.contains("${entity.label.create}")) {
					line = line.replace("${entity.label.create}", entityCreateLabel);
				}
				if (line.contains("${entity.label.list}")) {
					line = line.replace("${entity.label.list}", entityListLabel);
				}
				if (line.contains("${entity.label.edit}")) {
					line = line.replace("${entity.label.edit}", entityEditLabel);
				}
				if (line.contains("${entity.label.detail}")) {
					line = line.replace("${entity.label.detail}", entityDetailLabel);
				}
				if (line.contains("${entity.label.delete}")) {
					line = line.replace("${entity.label.delete}", entityDeleteLabel);
				}
				if (line.contains("${entity.id.var}")) {
					line = line.replace("${entity.id.var}", entityCamelCase + "Id");
				}
				newLines.add(line);
			}

			objectContent = "";
			for (String line : newLines) {
				objectContent += line + "\n";
			}

			LOG.info("*************************************************");
			// LOG.info(objectContent);
//			String teste = "";
			LOG.info("-------------------------------------------------");

			int beginFieldsTemplate = objectContent.indexOf("${partial.fields.begin}");
			int endFieldsTemplate = objectContent.indexOf("${partial.fields.end}") + 21;

			String templateFields = objectContent.substring(beginFieldsTemplate, endFieldsTemplate);
			String templateFieldsFinal = templateFields;
			templateFieldsFinal = templateFieldsFinal.replace("${partial.fields.begin}", "");
			templateFieldsFinal = templateFieldsFinal.replace("${partial.fields.end}", "");
			LOG.info(templateFields);
			String finalLines = "";

			for (AvaliableProperties item : selected.getProperties()) {

				if (StringUtils.isEmpty(item.getValue())) {
					String tmp = "";
					if (!item.getIsId()) {
						tmp = templateFieldsFinal.replace("${entity.field}", item.getName());
						tmp = tmp.replace("${entity.var}", entityCamelCase);
						tmp = tmp.replace("${view.beanForm}", beanClassView);
						tmp = tmp.replace("${optional.render.begin}", "");
						tmp = tmp.replace("${optional.render.end}", "");						
						tmp = tmp.replace("${optional.hidden}", "");
						tmp = tmp.replace("${optional.disable}", "");
					} else {
						tmp = templateFieldsFinal.replace("${entity.field}", item.getName());
						tmp = tmp.replace("${entity.var}", entityCamelCase);
						tmp = tmp.replace("${view.beanForm}", beanClassView);
						tmp = tmp.replace("${optional.render.begin}", String.format("<ui:fragment rendered=\"#{not empty %s.%s.%s}\">",beanClassView, entityCamelCase, item.getName()));
						tmp = tmp.replace("${optional.render.end}", "</ui:fragment>");
						tmp = tmp.replace("${optional.hidden}", String.format("<h:inputHidden value=\"#{%s.%s.%s}\" />",beanClassView, entityCamelCase, item.getName()));
						tmp = tmp.replace("${optional.disable}", "disabled=\"true\"");						
					}
					finalLines += tmp + "\n";
				}
			}

			objectContent = objectContent.replace(templateFields, finalLines);

			LOG.info("*************************************************");
			LOG.info(objectContent);

			// String baseViewFormPath = baseViewFolderPath.concat("/form.xhtml");

			File baseViewFolderPathDir = new File(baseViewFolderPath);
			if (!baseViewFolderPathDir.exists()) {
				baseViewFolderPathDir.mkdirs();
			}

			try {
				FileWriter fileWriter = new FileWriter(baseViewFormPath);
				fileWriter.write(objectContent);
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("error.file.fail_write"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			MessageUtil.addErrorMessage("Falha", "Erro ao gerar o Scaffold para a view de formulário");
		}
	}

	public void generateViewList() {
		try {
			if (selected.getProperties().isEmpty()) {
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"),
						MessageUtil.getMsg("error.scaffold.not_found"));
			}
			String basePath = propertiesUtil.get(SCAFFOLD_BASE_PATH);
			String baseViewPath = propertiesUtil.get(SCAFFOLD_VIEW_PATH);
			String baseViewFolderName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, selected.getName());
			String baseViewFolderPath = baseViewPath + "/" + baseViewFolderName;
			String baseViewListPath = baseViewFolderPath + "/index.xhtml";
			String modelParcialBeanPath = propertiesUtil.get(SCAFFOLD_GENERATE_MODELS_PATH);
			String modelViewListPath = basePath.concat(modelParcialBeanPath.concat("/index.txt"));
			String entityCreateLabel = "";
			String entityListLabel = "";
			String entityCamelCase = "";
			String entityEditLabel = "";
			String entityDetailLabel = "";
			String entityDeleteLabel = "";
			LOG.info("---------------------DADOS---------------------");
			LOG.info("basePath " + basePath);
			LOG.info("baseViewPath " + baseViewPath);
			LOG.info("baseViewFolderName " + baseViewFolderName);
			LOG.info("baseViewFolderPath " + baseViewFolderPath);
			LOG.info("modelParcialBeanPath " + modelParcialBeanPath);
			LOG.info("modelViewListPath " + modelViewListPath);

			for (AvaliableProperties entityLine : selected.getProperties()) {

				if (entityLine.getType() == "entityCamelCase") {
					entityCamelCase = entityLine.getValue();
				}
				if (entityLine.getName() == "entityCreate") {
					entityCreateLabel = entityCamelCase + "." + entityLine.getName();
				}
				if (entityLine.getName() == "entityList") {
					entityListLabel = entityCamelCase + "." + entityLine.getName();
				}
				if (entityLine.getName() == "entityEdit") {
					entityEditLabel = entityCamelCase + "." + entityLine.getName();
				}
				if (entityLine.getName() == "entityDetail") {
					entityDetailLabel = entityCamelCase + "." + entityLine.getName();
				}
				if (entityLine.getName() == "entityDelete") {
					entityDeleteLabel = entityCamelCase + "." + entityLine.getName();
				}

				System.out.println("------------------------------");
				System.out.println(entityLine);

			}

			String beanClass = selected.getName().concat("Bean");
			String beanClassView = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, beanClass);
			List<String> lines = readFile(modelViewListPath, false);
			List<String> newLines = new ArrayList<String>();

			for (String line : lines) {
				if (line.contains("${view.beanList}")) {
					line = line.replace("${view.beanList}", beanClassView);
				}
				if (line.contains("${view.beanList}")) {
					line = line.replace("${view.beanList}", beanClassView);
				}
				if (line.contains("${entity.var}")) {
					line = line.replace("${entity.var}", entityCamelCase);
				}
				if (line.contains("${entity.label.create}")) {
					line = line.replace("${entity.label.create}", entityCreateLabel);
				}
				if (line.contains("${entity.label.list}")) {
					line = line.replace("${entity.label.list}", entityListLabel);
				}
				if (line.contains("${entity.label.edit}")) {
					line = line.replace("${entity.label.edit}", entityEditLabel);
				}
				if (line.contains("${entity.label.detail}")) {
					line = line.replace("${entity.label.detail}", entityDetailLabel);
				}
				if (line.contains("${entity.label.delete}")) {
					line = line.replace("${entity.label.delete}", entityDeleteLabel);
				}
				newLines.add(line);
			}

			objectContent = "";
			for (String line : newLines) {
				objectContent += line + "\n";
			}

			LOG.info("*************************************************");
			// LOG.info(objectContent);
//				String teste = "";
			LOG.info("-------------------------------------------------");

			int beginFieldsTemplate = objectContent.indexOf("${partial.fields.begin}");
			int endFieldsTemplate = objectContent.indexOf("${partial.fields.end}") + 21;

			String templateFields = objectContent.substring(beginFieldsTemplate, endFieldsTemplate);
			String templateFieldsFinal = templateFields;
			templateFieldsFinal = templateFieldsFinal.replace("${partial.fields.begin}", "");
			templateFieldsFinal = templateFieldsFinal.replace("${partial.fields.end}", "");
			LOG.info(templateFields);
			String finalLines = "";

			for (AvaliableProperties item : selected.getProperties()) {

				if (StringUtils.isEmpty(item.getValue()) && !item.getIsId()) {

					String tmp = templateFieldsFinal.replace("${entity.field}", item.getName());
					tmp = tmp.replace("${entity.var}", entityCamelCase);
					tmp = tmp.replace("${view.beanList}", beanClassView);

					finalLines += tmp + "\n";

				}
				/*
				 * <p:outputLabel for="${entity.field}"
				 * value="#{lb['${entity.var}.${entity.field}']}" /> <p:inputText
				 * id="${entity.field}"
				 * value="#{${view.beanList}.${entity.var}.${entity.field}}" />
				 */
			}
			objectContent = objectContent.replace(templateFields, finalLines);

			LOG.info("*************************************************");
			LOG.info(objectContent);

			File baseViewFolderPathDir = new File(baseViewFolderPath);
			if (!baseViewFolderPathDir.exists()) {
				baseViewFolderPathDir.mkdirs();
			}

			try {
				FileWriter fileWriter = new FileWriter(baseViewListPath);
				fileWriter.write(objectContent);
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("error.file.fail_write"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			MessageUtil.addErrorMessage("Falha", "Erro ao gerar o Scaffold para a view de listagem");
		}
	}

	public void generateMenuItem() {
		try {
			if (selected.getProperties().isEmpty()) {
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"),
						MessageUtil.getMsg("error.scaffold.not_found"));
			}
			objectContent = null;
			System.out.println("#########################");
			System.out.println(SCAFFOLD_MENU_FILE_PATH);
			System.out.println("#########################");
			String menuItemPath = propertiesUtil.get(SCAFFOLD_MENU_FILE_PATH);
			System.out.println(menuItemPath);
//			if (menuItemPath != null && !menuItemPath.isEmpty()) {
//				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"),MessageUtil.getMsg("scaffold.menu_item_config.not_found"));
//			}			
			String basePath = propertiesUtil.get(SCAFFOLD_BASE_PATH);
			String markerMenu = propertiesUtil.get(SCAFFOLD_MENU_MARK_ITEM);
			String modelParcialBeanPath = propertiesUtil.get(SCAFFOLD_GENERATE_MODELS_PATH);
			String modelItemMenuPath = basePath.concat(modelParcialBeanPath.concat("/itemMenu.txt"));
			String baseViewPath = propertiesUtil.get(SCAFFOLD_VIEW_PATH);
			String baseViewFolderName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, selected.getName());
			String baseViewFolderPath = baseViewPath + "/" + baseViewFolderName;
			// String baseViewListPath = baseViewFolderPath + "/index.xhtml";
			// String modelItemMenuPath = propertiesUtil.get(SCAFFOLD_BASE_PATH);
			String entityCreateLabel = "";
			String entityListLabel = "";
			String entityCamelCase = "";
			LOG.info("---------------------DADOS---------------------");
			LOG.info("basePath " + basePath);
			LOG.info("baseViewPath " + baseViewPath);
			LOG.info("baseViewFolderName " + baseViewFolderName);
			LOG.info("baseViewFolderPath " + baseViewFolderPath);
			LOG.info("modelParcialBeanPath " + modelParcialBeanPath);
//	        LOG.info("modelViewListPath "+modelViewListPath);	
			LOG.info("menuItemPath " + menuItemPath);
			LOG.info("modelItemMenuPath " + modelItemMenuPath);

			for (AvaliableProperties entityLine : selected.getProperties()) {
				if (entityLine.getType() == "entityCamelCase") {
					entityCamelCase = entityLine.getValue();
				}
				if (entityLine.getName() == "entityCreate") {
					entityCreateLabel = entityCamelCase + "." + entityLine.getName();
				}
				if (entityLine.getName() == "entityList") {
					entityListLabel = entityCamelCase + "." + entityLine.getName();
				}
			}

			// String beanClass = selected.getName().concat("Bean");
			// String beanClassView = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL,
			// beanClass);
			List<String> lines = readFile(modelItemMenuPath, false);
			List<String> newLines = new ArrayList<String>();

			// System.out.println(lines);

			for (String line : lines) {
				if (line.contains("${entity.var}")) {
					line = line.replace("${entity.var}", entityCamelCase);
				}
				if (line.contains("${entity.label.create}")) {
					line = line.replace("${entity.label.create}", entityCreateLabel);
				}
				if (line.contains("${entity.label.list}")) {
					line = line.replace("${entity.label.list}", entityListLabel);
				}
				newLines.add(line);
			}
			System.out.println("-----------------------------");
			System.out.println("markerMenu " + markerMenu);
			System.out.println("-----------------------------");
			String objectContentNewItem = markerMenu + "\n";
			for (String line : newLines) {
				objectContentNewItem += line + "\n";
			}

			List<String> lines1 = readFile(menuItemPath, false);
			List<String> newLines1 = new ArrayList<String>();
			boolean menuItemExist = false;
			for (String line : lines1) {
				if (line.contains(String.format("<li class=\"nav-item has-treeview\" id=\"item-menu-scaffold-%s\">", entityCamelCase)) == true) {
					menuItemExist = true;
				}
				newLines1.add(line);
			}
//			objectContent = "";
//			for (String line : newLines) {
//				objectContent += line + "\n";
//			}        
//	        
//			String objectContent1 = "";
//			for (String line : newLines1) {
//				objectContent += line + "\n";
//			}
			String objectFinalMenuContent = "";
			if (menuItemExist == false) {
				System.out.println("ai!---------------------------------------------");
				System.out.println(objectContentNewItem);
				System.out.println("oi?---------------------------------------------");
				for (String line : newLines1) {
					objectFinalMenuContent += line + "\n";
				}
				objectContent = objectFinalMenuContent.replace(markerMenu, objectContentNewItem);
				System.out.println("E aí?---------------------------------------------");
				System.out.println(objectFinalMenuContent);
			}

			System.out.println("Agora que já tenho o modelo pronto , tenho que sobrescrever no arquivo de menu");

			System.out.println(objectContent);
			System.out.println();
			System.out.println("menuItemPath " + menuItemPath);

			try {
				FileWriter fileWriter = new FileWriter(menuItemPath);
				fileWriter.write(objectContent);
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("error.file.fail_write"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageUtil.addErrorMessage("Falha", "Erro ao gerar o Scaffold para os itens de menu");
		}
	}

	public void generateLabels() {
		System.out.println(selected);
		if (selected.getProperties().isEmpty()) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("error.scaffold.not_found"));
		}

		String labelPath = propertiesUtil.get(SCAFFOLD_LABEL_PATH);
		System.out.println("######################");
		System.out.println(labelPath);
		System.out.println("######################");
		if (StringUtils.isEmpty(labelPath)) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"),
					MessageUtil.getMsg("scaffold.labels_config.not_found"));
		}

		List<String> lines = readFile(labelPath, true);
		List<String> newLines = new ArrayList<String>();
		List<AvaliableProperties> properties = selected.getProperties();
		for (String line : lines) {
			newLines.add(line);
		}

		for (AvaliableProperties item : properties) {
			if (!StringUtils.isEmpty(item.getValue())) {
				continue;
			}

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
			if (fieldExist == false) {
				newLines.add(newLine);
			}
		}

		for (AvaliableProperties item : properties) {
			if (StringUtils.isEmpty(item.getValue()) || item.getType().contains("package")) {
				continue;
			}

			String formattedName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, item.getValue());
			formattedName = formattedName.replace("-", " ");
			formattedName = StringUtils.capitalize(formattedName);
			String newLine = String.format("%s.%s=%s", selected.getName().toLowerCase(), item.getName(), formattedName);
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
		List<String> lines = readFile(selected.getPath(), true);
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
	}

	private List<String> readFile(String objectPath, Boolean clear) {
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

}
