/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import se.altrusoft.docserv.models.TemplateModel;

public class Global extends GlobalSettings {
	private static final String APPLICATION_CONTEXT = "applicationContext.xml";
	
	private static final String CONFIG_PROPERTIES_FILENAME = "config.properties";
	
	/* Enable in order to allow configuration file in user directory
	private static final String USERDIR_CONFIG_PROPERTIES_FILENAME = "docserv_" + CONFIG_PROPERTIES_FILENAME;
	*/
	
	private static PropertiesConfiguration configPropertiesFromConfigFile = null;
	
	private AbstractApplicationContext applicationContext;
	

	@Override
	public void onStart(Application app) {
		Logger.info("Application startup ...");
		resourcesTest();
		initializeApplicationContext();
	}
	
	@Override
	public void onStop(Application app) {
		Logger.info("Application shutdown... ");
	}

	@Override
	public <A> A getControllerInstance(Class<A> clazz) {
		return applicationContext.getBean(clazz);
	}

	/**
	 * Read a configuration parameter.
	 * Parameters from configuration properties file has precedence over 
	 * a parameter defined in Application.conf
	 * 
	 * @param paramName
	 * @return
	 */
	public static final String getConfigParam(String paramName) {
		PropertiesConfiguration properties = getConfigPropertiesFromConfigFile();
		String result = null;
		if (properties.containsKey(paramName)) {
			result = (String) properties.getProperty(paramName);
		}
		if (result == null) /* || result.isEmpty()) ???? */{
			result = getConfigParamFromApplicationConfig(paramName);
		}
		return result;
	}
	
	/**
	 * Read a configuration parameter from Application.conf 
	 * @param paramName
	 * @return
	 */
	public static final String getConfigParamFromApplicationConfig(String paramName) {
		return Play.application().configuration().getString(paramName);
	}
	
	private void initializeApplicationContext() {
		String configDirName = getConfigParam("docserv.config.dir");

		File globalApplicationContext = new File(configDirName, APPLICATION_CONTEXT);

		if (globalApplicationContext.exists()) {
			Logger.info("Using global application context in " + configDirName);
			applicationContext = new ClassPathXmlApplicationContext("file://"
					+ globalApplicationContext.getAbsolutePath());
		} else {
			Logger.info("Using application context from application");
			applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT);
		}

		try {
			// Search for resources in docserv.config.dir
			URL[] classLoaderURLs = new URL[1];
			classLoaderURLs[0] = new URL("file://"
					+ globalApplicationContext.getParent() + "/");
			URLClassLoader classLoader = new URLClassLoader(classLoaderURLs,
					applicationContext.getClassLoader());

			applicationContext.setClassLoader(classLoader);
			applicationContext.refresh();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}		
	}
		
	private static PropertiesConfiguration getConfigPropertiesFromConfigFile() {
		
		if(configPropertiesFromConfigFile == null) {
			configPropertiesFromConfigFile=new PropertiesConfiguration();
			String configDirName = getConfigParamFromApplicationConfig("docserv.config.dir");
			File configDir = new File(configDirName);
	
			if (configDir.isDirectory()) {
				loadPropertiesFromFile(configDir, CONFIG_PROPERTIES_FILENAME);
				
			/* Enable in order to allow configuration file in user directory
			} else {
				File userDir = new File(SystemUtils.USER_DIR);
				if (userDir.isDirectory()) {
					loadPropertiesFromFile(userDir, USERDIR_CONFIG_PROPERTIES_FILENAME);
				}
			*/	
			}
		}
		return configPropertiesFromConfigFile;
	}
	
	private static final void loadPropertiesFromFile(File configDir, String propertiesFileName) {
		
		File propertiesFile = new File(configDir, propertiesFileName);
		if (!(propertiesFile.exists() && propertiesFile.canRead())) {
			Logger.warn("Unable to access config file: " + propertiesFile);
		} else {
			try {
				configPropertiesFromConfigFile.load(propertiesFile);
				Logger.info("Properties loaded from file " + propertiesFile);
			} catch (ConfigurationException e) {
				Logger.warn("Unbable to read/load config file: " + propertiesFile, e);
			}
		}	
	}
	
	/**
	 * TODO:
	 * We are moving away from Spring framwork and mowing towards a design with
	 * pluggable Template Models. First step is to remove Spring then migrate to 
	 * Play 2.4 with dependency injection.
	 * Below test code is intended to b placed in TemplateModelFactory in some
	 * later stage of development. /HH  
	 */
	public void resourcesTest() {
		File templates = Play.application().getFile("public/templates");
		for(File templateDir : templates.listFiles()) {
			Logger.info("Found template: " + templateDir.getName());
			PropertiesConfiguration templateProperties = new PropertiesConfiguration();
			File templatePropertiesFile = new File(templateDir, "template.config");
			if (templatePropertiesFile.exists() && templatePropertiesFile.canRead()) {
				try {
					templateProperties.load(templatePropertiesFile);
					String templateName=(String) templateProperties.getProperty("name");
					if(templateName != null) {
						Logger.info("Template is named : " + templateName);
					}
					String templateType=(String) templateProperties.getProperty("type");
					if(templateType != null) {
						Logger.info("Template has type : " + templateType);
					}
					String templateClassName=(String) templateProperties.getProperty("class");
					if(templateClassName != null) {
						Logger.info("Template has class name : " + templateClassName);
						Class<?> clazz = null;
						
						try {
							clazz = Class.forName(templateClassName);
							
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						if (clazz != null) {
							Logger.info("Template has class : " + clazz.getCanonicalName());
							Class<? extends TemplateModel> templateClazz = null;
							try {
								templateClazz = (Class<? extends TemplateModel>) clazz;
							} catch (Exception e) {
								Logger.info("Template class is not a template class");
								e.printStackTrace();
							}
							if (clazz != null) {
								Logger.info("Template class is a template class");
								TemplateModel templateModel = null;
								try {
									templateModel = templateClazz.newInstance();
								} catch (InstantiationException | IllegalAccessException e) {
									e.printStackTrace();
								}
								if (templateModel != null) {
									Logger.info("Template model may be instantiated");
								}
							}
						}
					}
					String templateFileName=(String) templateProperties.getProperty("templateFile");
					if(templateFileName != null) {
						Logger.info("Template file name : " + templateFileName);
						File templateFile = Play.application().getFile(templateFileName);
						if (templateFile.exists()) {
							Logger.info("Template file named : " + templateFileName + " exists");
						}
						if (templateFile.canRead()) {
							Logger.info("Template file named : " + templateFileName + " is readable");
						}
						if (templateFile.isFile()) {
							Logger.info("Template file named : " + templateFileName + " is a file");
						}
					}

				} catch (ConfigurationException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
