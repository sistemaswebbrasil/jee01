package br.com.siswbrasil.jee01.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class PropertiesUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	public String get(String param) {
		Properties prop = this.loadPropertiesFile("application.properties");
		return (String) prop.get(param);
	}

	public Properties loadPropertiesFile(String filePath) {

		Properties prop = new Properties();
		try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(filePath)) {
			prop.load(resourceAsStream);
		} catch (IOException e) {
			System.err.println("Unable to load properties file : " + filePath);
		}

		return prop;

	}

}
