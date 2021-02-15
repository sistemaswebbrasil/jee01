package br.com.siswbrasil.jee01.scaffold;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.siswbrasil.jee01.scaffold.model.AvailableObject;
import br.com.siswbrasil.jee01.util.MessageUtil;
import br.com.siswbrasil.jee01.util.PropertiesUtil;

@Stateless
public class ScaffoldService {

	@Inject
	private Logger LOG;

	@Inject
	private PropertiesUtil propertiesUtil;

	private String finalEntityPath;

	private List<AvailableObject> entities = new ArrayList<AvailableObject>();

	public static final String SCAFFOLD_BASE_PATH = "scaffold.base.path";
	public static final String SCAFFOLD_ENTITY_PATH = "scaffold.entity.path";

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

}
