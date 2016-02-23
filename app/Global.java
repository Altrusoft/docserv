/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.context.support.AbstractApplicationContext;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;

public class Global extends GlobalSettings {
	//private static final String APPLICATION_CONTEXT = "applicationContext.xml";
	
	private static final String CONFIG_PROPERTIES_FILENAME = "config.properties";
	
	/* Enable in order to allow configuration file in user directory
	private static final String USERDIR_CONFIG_PROPERTIES_FILENAME = "docserv_" + CONFIG_PROPERTIES_FILENAME;
	*/
	
	private static PropertiesConfiguration configPropertiesFromConfigFile = null;
	
	private AbstractApplicationContext applicationContext;
	

	@Override
	public void onStart(Application app) {
		Logger.info("Application startup ...");
//		initializeApplicationContext();
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
	
//	private void initializeApplicationContext() {
//		String configDirName = getConfigParam("docserv.config.dir");
//
//		File globalApplicationContext = new File(configDirName, APPLICATION_CONTEXT);
//
//		if (globalApplicationContext.exists()) {
//			Logger.info("Using global application context in " + configDirName);
//			applicationContext = new ClassPathXmlApplicationContext("file://"
//					+ globalApplicationContext.getAbsolutePath());
//		} else {
//			Logger.info("Using application context from application");
//			applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT);
//		}
//
//		try {
//			// Search for resources in docserv.config.dir
//			URL[] classLoaderURLs = new URL[1];
//			classLoaderURLs[0] = new URL("file://"
//					+ globalApplicationContext.getParent() + "/");
//			URLClassLoader classLoader = new URLClassLoader(classLoaderURLs,
//					applicationContext.getClassLoader());
//
//			applicationContext.setClassLoader(classLoader);
//			applicationContext.refresh();
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}		
//	}
		
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
	
}
