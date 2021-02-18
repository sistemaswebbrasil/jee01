package br.com.siswbrasil.jee01.scaffold.bean;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CaseFormat;

import br.com.siswbrasil.jee01.exception.ScaffoldException;
import br.com.siswbrasil.jee01.scaffold.ScaffoldService;
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
	public static final String SCAFFOLD_DATA_MODEL_PATH = "scaffold.data.model.path";
	public static final String SCAFFOLD_VIEW_PATH = "scaffold.view.path";
	public static final String SCAFFOLD_LABEL_PATH = "scaffold.labels_pt_BR.path";
	public static final String SCAFFOLD_GENERATE_MODELS_PATH = "scaffold.generate_models.path";
	public static final String SCAFFOLD_MENU_FILE_PATH = "scaffold.menu_file.path";
	public static final String SCAFFOLD_MENU_MARK_ITEM = "scaffold.menu_mark_item.value";

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
	
	public void generateLabels() {		
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

			String entityPackage = "";
			String entityCreateLabel = "";
			String entityListLabel = "";
			String idType = "";
			String entityCamelCase = "";
			String entityEditLabel = "";
			String entityDetailLabel = "";
			String entityDeleteLabel = "";
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
			}

			String beanClass = selected.getName().concat("Bean");
			String beanClassView = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, beanClass);

			List<String> lines = scaffoldService.readFile(modelViewFormPath, false);
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
						tmp = tmp.replace("${optional.render.begin}",
								String.format("<ui:fragment rendered=\"#{not empty %s.%s.%s}\">", beanClassView,
										entityCamelCase, item.getName()));
						tmp = tmp.replace("${optional.render.end}", "</ui:fragment>");
						tmp = tmp.replace("${optional.hidden}", String.format("<h:inputHidden value=\"#{%s.%s.%s}\" />",
								beanClassView, entityCamelCase, item.getName()));
						tmp = tmp.replace("${optional.disable}", "disabled=\"true\"");
					}
					finalLines += tmp + "\n";
				}
			}

			objectContent = objectContent.replace(templateFields, finalLines);
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
			}

			String beanClass = selected.getName().concat("Bean");
			String beanClassView = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, beanClass);
			List<String> lines = scaffoldService.readFile(modelViewListPath, false);
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
					tmp = tmp.replace("${entity.var}", entityCamelCase);
					tmp = tmp.replace("${view.beanList}", beanClassView);

					finalLines += tmp + "\n";
				}
			}
			objectContent = objectContent.replace(templateFields, finalLines);
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

	public void generateViewListLazy() {
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
			String modelViewListPath = basePath.concat(modelParcialBeanPath.concat("/indexLazy.txt"));
			String entityCreateLabel = "";
			String entityListLabel = "";
			String entityCamelCase = "";
			String entityEditLabel = "";
			String entityDetailLabel = "";
			String entityDeleteLabel = "";
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
			}

			String beanClass = selected.getName().concat("Bean");
			String beanClassView = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, beanClass);
			List<String> lines = scaffoldService.readFile(modelViewListPath, false);
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
					tmp = tmp.replace("${entity.var}", entityCamelCase);
					tmp = tmp.replace("${view.beanList}", beanClassView);

					finalLines += tmp + "\n";

				}
			}

			objectContent = objectContent.replace(templateFields, finalLines);
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
			String menuItemPath = propertiesUtil.get(SCAFFOLD_MENU_FILE_PATH);

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
			String entityEditLabel = "";
			String entityDetailLabel = "";
			String entityDeleteLabel = "";
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
			List<String> lines = scaffoldService.readFile(modelItemMenuPath, false);
			List<String> newLines = new ArrayList<String>();

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
				if (line.contains("${entity.resources}")) {
					line = line.replace("${entity.resources}", baseViewFolderName);
				}
				newLines.add(line);
			}
			String objectContentNewItem = markerMenu + "\n";
			for (String line : newLines) {
				objectContentNewItem += line + "\n";
			}

			List<String> lines1 = scaffoldService.readFile(menuItemPath, false);
			List<String> newLines1 = new ArrayList<String>();
			boolean menuItemExist = false;
			for (String line : lines1) {
				if (line.contains(String.format("<li class=\"nav-item has-treeview\" id=\"item-menu-scaffold-%s\">",
						entityCamelCase)) == true) {
					menuItemExist = true;
				}
				newLines1.add(line);
			}

			String objectFinalMenuContent = "";
			if (menuItemExist == false) {
				for (String line : newLines1) {
					objectFinalMenuContent += line + "\n";
				}
				objectContent = objectFinalMenuContent.replace(markerMenu, objectContentNewItem);
			}

			if (StringUtils.isNotBlank(objectContent.trim())) {
				try {
					FileWriter fileWriter = new FileWriter(menuItemPath);
					fileWriter.write(objectContent);
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
					MessageUtil.addErrorMessage(MessageUtil.getMsg("error"),
							MessageUtil.getMsg("error.file.fail_write"));
				}
			} else {
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"),
						MessageUtil.getMsg("error.file.content.empty"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageUtil.addErrorMessage("Falha", "Erro ao gerar o Scaffold para os itens de menu");
		}
	}

}
