package se.altrusoft.docserv.models;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import net.sf.corn.cps.CPScanner;
import net.sf.corn.cps.ResourceFilter;
import play.Logger;
import se.altrusoft.docserv.odsprocessor.DOMTransformer;
import se.altrusoft.docserv.odsprocessor.MarkupODSProcessor;
import se.altrusoft.docserv.odsprocessor.SimpleODSProcessor;

public class TemplateModelFactory {
	
	private Map<String, PropertiesConfiguration> templateConfigs;
	
	public TemplateModelFactory() {
		templateConfigs = new HashMap<>();
		List<URL> templateModelConfigFiles = CPScanner
				.scanResources(new ResourceFilter().packageName("public.templates.*").resourceName("template*.config"));
		for (URL u : templateModelConfigFiles) {
			try {
				InputStream in = u.openStream();
				PropertiesConfiguration templateProperties = new PropertiesConfiguration();
				try {
					templateProperties.load(in);
					String templateName = (String) templateProperties.getProperty("name");
					if (templateName != null) {
						if (templateConfigs.containsKey(templateName)) {
							Logger.error("Found duplicate tempalte configuration  : " + templateName + " in : " +u.toString());
						} else {
							templateConfigs.put(templateName, templateProperties);
							Logger.info("Loaded config for template  : " + templateName);
							// Testing:
							Optional<? extends TemplateModel> templateModel = getOptionalTemaplateModel(templateName);
							if(templateModel.isPresent()) {
								Logger.info("Succesfully tested to instantiate template model : " + templateName);
							} else {
								Logger.error("Unable to instantiate template model : " + templateName);
							}
						}
					}
				} catch (ConfigurationException e) {
					Logger.error("Failed to load tempalte configuration from : " + u.toString());
					e.printStackTrace();
				}
			} catch (IOException e) {
				Logger.error("Failed to open template config file : " + u.toString());
				e.printStackTrace();
			}
		}
	}
	
	
	public TemplateModel getTemplateModel(String templateName) {
		return getOptionalTemaplateModel(templateName).get();
	}
	

	private Optional<? extends TemplateModel> getOptionalTemaplateModel(String name) {
		Optional<? extends TemplateModel> templateModel = getUninjectedTemplateModel(name);
		if(templateModel != null && templateModel.isPresent()) {
			TemplateModel m = templateModel.get();
			
			/* Inject Template type */
			Optional<String> prop = getTemplateProperty(name,"type");
			if(prop != null && prop.isPresent()) {
				try {
					m.setTemplateType(TemplateType.valueOf(prop.get()));
					Logger.info("Using template type " + prop.get() + " for template  : " + name);
				} catch (Throwable e) {
					Logger.error("Invalid template type " + prop.get() + " specified for template  : " + name);
				}		
			} else {
				Logger.warn("No type specified for template  : " + name);
			}
			
			/* Inject Template File */
			prop = getTemplateProperty(name,"templateFile");
			if(prop != null && prop.isPresent()) {
				m.setTemplateFileName(prop.get());
				Logger.info("Using template file " + prop.get() + " for template  : " + name);
			}
			
			/* Inject Post processor */			
			//TODO: Fix this stupid implementation
			prop = getTemplateProperty(name,"postProcessor");
			if(prop != null && prop.isPresent()) {
				if (prop.get().equalsIgnoreCase("se.altrusoft.docserv.odsprocessor.SimpleODSProcessor")) {
					DOMTransformer transformer = new SimpleODSProcessor();
					m.setPostProcessor(transformer);
				} else if (prop.get().equalsIgnoreCase("se.altrusoft.docserv.odsprocessor.MarkupODSProcessor")) {
					DOMTransformer transformer = new MarkupODSProcessor();
					m.setPostProcessor(transformer);
					Logger.info("Using post processor " + prop.get() + " for template  : " + name);
				} else {
					Logger.error("Invalid postProcessor " + prop.get() + " specified for template  : " + name);
				}
				
			}
			return Optional.of(m);
		}
		return Optional.empty();
	}
	
	public Optional<? extends TemplateModel> getUninjectedTemplateModel(String templateName) {
		Class<?> templateModelClass = getTemplateModelClass(templateName);
		if (templateModelClass == null) {
			Logger.error("Failed to get class for template  : " + templateName);
			return null; 
//		} else if (! templateModelClass.isPresent()) {
//			Logger.warn("Failed to get class for template  : " + templateName);
//			return Optional.empty();
		}
		try {
			@SuppressWarnings("unchecked")
			Class<? extends TemplateModel> clazz = (Class<? extends TemplateModel>) templateModelClass;
			return Optional.of(clazz.newInstance());
		} catch (Throwable e) {
			Logger.error("Failed to instantiate template model : " + templateName);
			return null;
		}
	}
	
	private Class<?> getTemplateModelClass(String templateName) {
		Optional<String> templateClassName= getTemplateProperty(templateName, "class");
		if (templateClassName == null) {
			Logger.error("Failed to get class name from template properies for template  : " + templateName);
			return null; 
		} else if (! templateClassName.isPresent()) {
			Logger.warn("Failed to get class name from template properies for template  : " + templateName);
			return null;
		}
		try {
			//Class<?> clazz = Class.forName(templateClassName.get());
			//Class<? extends TemplateModel> result = (Class<? extends TemplateModel>) clazz;
			return Class.forName(templateClassName.get());
		} catch (Throwable e) {
			Logger.error("Failed to get class from template  : " + templateClassName);
			return null;
		}
	}

	private Optional<String> getTemplateProperty(String templateName, String propertyName) {
		PropertiesConfiguration templateProperties = templateConfigs.get(templateName);
		if (templateProperties == null) {
			return null;
		}
		String result=(String) templateProperties.getProperty(propertyName);
		if (result == null) {
			return Optional.empty(); 
		}
		return Optional.of(result);
	}
	

	
}
