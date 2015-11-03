/*
 * Copyright (c) 2015 Altrusoft AB. This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain
 * one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.models;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import play.Logger;
import se.altrusoft.docserv.odsprocessor.DOMTransformer;

public abstract class TemplateModel {

	private static final String DEFAULT_PARAMETER_PREFIX = "data";

	private UUID id;
	private String name;
	private File templateFile;
	private File jsFile;
	private DOMTransformer postProcessor;
	private TemplateType templateType;
	private Properties properties;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public File getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(File templateFile) {
		this.templateFile = templateFile;
	}

	public File getJsFile() {
		return jsFile;
	}

	public void setJsFile(File jsFile) {
		this.jsFile = jsFile;
	}

	public String getInTemplateDesignation() {
		return DEFAULT_PARAMETER_PREFIX;
	}

	public String[] fieldsToTranslate() {
		return new String[] {};
	}

	public void expandModel() {
		// normal case do nothing;
	}

	public void setField(String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
		Field field = getDeclaredField(fieldName);
		field.set(this, value);
	}

	public String getField(String fieldName) throws NoSuchFieldException, IllegalAccessException {
		Field field = getDeclaredField(fieldName);
		return (String) field.get(this);
	}

	@SuppressWarnings("unchecked")
	public List<String> getListField(String fieldName) throws NoSuchFieldException, IllegalAccessException {
		Field field = getDeclaredField(fieldName);
		return (List<String>) field.get(this);
	}

	public boolean fieldIsArray(String fieldName) throws NoSuchFieldException, IllegalAccessException {
		Field field = getDeclaredField(fieldName);
		return (field.get(this) instanceof List<?>);
	}

	public void translateProperties() {
		for (String fieldName : this.fieldsToTranslate()) {
			try {
				if (fieldIsArray(fieldName)) {
					boolean translatedValueExists = false;
					List<String> untranslatedValues = getListField(fieldName);
					List<String> translatedValues = new ArrayList<String>();
					for (String untranslatedValue : untranslatedValues) {
						String key = fieldName + ".option." + untranslatedValue;
						if (properties.containsKey(key)) {
							translatedValues.add((String) properties.get(key));
							translatedValueExists = true;
						} else {
							translatedValues.add(untranslatedValue);
						}
					}
					if (translatedValueExists) {
						this.setField(fieldName, translatedValues);
					}
				} else {
					String untranslatedValue = this.getField(fieldName);
					String key = fieldName + ".option." + untranslatedValue;
					if (properties.containsKey(key)) {
						String translatedValue = properties.getProperty(key);
						this.setField(fieldName, translatedValue);
					}
				}
			} catch (NoSuchFieldException e) {
				Logger.error("Failed to access field: " + fieldName + " of template model. (No such field)", e);
			} catch (IllegalAccessException e) {
				Logger.error("Failed to access field: " + fieldName + " of template model. (Illegal access)", e);
			}
		}
	}

	private Field getDeclaredField(String fieldName) throws NoSuchFieldException {
		return this.getClass().getDeclaredField(fieldName);
	}

	public DOMTransformer getPostProcessor() {
		return postProcessor;
	}

	public void setPostProcessor(DOMTransformer postProcessor) {
		this.postProcessor = postProcessor;
	}

	public TemplateType getTemplateType() {
		return templateType;
	}

	public void setTemplateType(TemplateType templateType) {
		this.templateType = templateType;
	}

	public boolean isDynamic() {
		return false;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}
