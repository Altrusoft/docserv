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
import org.apache.commons.lang.SystemUtils;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;

public class Global extends GlobalSettings {
	private static final String APPLICATION_CONTEXT = "applicationContext.xml";
	private static final String CONFIG_PROPERTIES_FILENAME = "config.properties";
	private AbstractApplicationContext applicationContext;

	@Override
	public void onStart(Application app) {
		Logger.info("Application startup ...");

		PropertiesConfiguration properties = new PropertiesConfiguration();
		String configDirName = getConfigParamString("docserv.config.dir",
				properties);

		File globalApplicationContext = new File(configDirName,
				APPLICATION_CONTEXT);

		if (globalApplicationContext.exists()) {
			Logger.debug("Using global application context");
			applicationContext = new ClassPathXmlApplicationContext("file://"
					+ globalApplicationContext.getAbsolutePath());
		} else {
			Logger.debug("Using local application context");
			applicationContext = new ClassPathXmlApplicationContext(
					APPLICATION_CONTEXT);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		injectConfiguration();
	}

	@Override
	public <A> A getControllerInstance(Class<A> clazz) {
		return applicationContext.getBean(clazz);
	}

	@Override
	public void onStop(Application app) {
		Logger.info("Application shutdown... ");
	}

	private static PropertiesConfiguration readPropertiesFromFile(
			File configDir, String propertiesFileName) {
		PropertiesConfiguration result = new PropertiesConfiguration();

		File propertiesFile = new File(configDir, propertiesFileName);
		if (!(propertiesFile.exists() && propertiesFile.canRead())) {
			// TODO: stop allowing defaults?
			Logger.warn("Unable to access config file: " + propertiesFile
					+ " causing: using only appliction default configuration");
		} else {
			try {
				result.load(propertiesFile);
				Logger.info("Properties read from file " + propertiesFile);
			} catch (ConfigurationException ce) {
				// TODO: stop allowing defaults?
				Logger.warn(
						"Unbable to read/load config file: "
								+ propertiesFile
								+ " causing: using only appliction default configuration",
						ce);
			}
		}

		return result;
	}

	private static void injectConfiguration() {
		final String ALTERNATIVE_CONFIG_PROPERTIES_FILENAME = "docserv_"
				+ CONFIG_PROPERTIES_FILENAME;
		Logger.debug("Initiating dependency injection");

		PropertiesConfiguration properties = new PropertiesConfiguration();
		String configDirName = getConfigParamString("docserv.config.dir",
				properties);
		Logger.debug("Looking in directory: " + configDirName
				+ " for config file: " + CONFIG_PROPERTIES_FILENAME);

		File configDir = new File(configDirName);
		// TODO: Instead of one file, allow union (general and override given
		// values in customized) ?
		if (configDir.isDirectory()) {
			properties = readPropertiesFromFile(configDir,
					CONFIG_PROPERTIES_FILENAME);
		} else {
			File alternativeConfigDir = new File(SystemUtils.USER_DIR);
			Logger.info("No directory found: " + configDir
					+ ". Looking in directory: " + alternativeConfigDir
					+ " for alternative config file: "
					+ ALTERNATIVE_CONFIG_PROPERTIES_FILENAME);
			if (alternativeConfigDir.isDirectory()) {
				properties = readPropertiesFromFile(alternativeConfigDir,
						ALTERNATIVE_CONFIG_PROPERTIES_FILENAME);
			} else {
				// TODO: stop allowing defaults?
				Logger.info("No alternative directory found: "
						+ alternativeConfigDir
						+ ". Using only appliction default configuration");
			}
		}
	}

	public static final String getConfigParamString(String paramName,
			PropertiesConfiguration properties) {
		String result = null;
		if (properties.containsKey(paramName)) {
			result = (String) properties.getProperty(paramName);
		}
		if (result == null) /* || result.isEmpty()) ???? */{
			result = Play.application().configuration().getString(paramName);
		}

		return result;
	}
}
