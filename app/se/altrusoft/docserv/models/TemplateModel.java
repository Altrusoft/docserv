/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import play.Logger;
import play.Play;
import se.altrusoft.docserv.odsprocessor.DOMTransformer;
import se.altrusoft.docserv.odsprocessor.ODTProcessor;

public abstract class TemplateModel {

	private static final String DEFAULT_PARAMETER_PREFIX = "data";

	private Resource templateFile;
	private String templateFileName;

	private DOMTransformer postProcessor;
	private TemplateType templateType;
	private Properties properties;

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
	
	public ByteArrayOutputStream generateDocument() throws XDocReportException, IOException {
		return generateDocument(Play.application());
	}

	public ByteArrayOutputStream generateDocument(play.Application application) throws XDocReportException, IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = null;
		try {
			in = application.resourceAsStream(getTemplateFileName());

			IXDocReport report = null;
			if (TemplateType.VELOCITY.equals(this.getTemplateType())) {
				report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);
			} else {
				report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Freemarker);
			}

			IContext context = report.createContext();
			context.put(this.getInTemplateDesignation(), this);

			if (this.getPostProcessor() != null) {
				ByteArrayOutputStream tempOutputStream = new ByteArrayOutputStream();
				report.process(context, tempOutputStream);
				InputStream tempInputStream = new ByteArrayInputStream(tempOutputStream.toByteArray());
				ODTProcessor.transformODT(tempInputStream, out, this.getPostProcessor());
			} else {
				report.process(context, out);
			}
		} finally {
			IOUtils.closeQuietly(in);
		}
		return out;
	}

	public void translateProperties() {
		// TODO: Simplify this by using BeanUtils
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

	public String getTemplateFileName() {
		return templateFileName;
	}

	public void setTemplateFileName(String templateFileName) {
		this.templateFileName = templateFileName;
	}

	public Resource getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(Resource templateFile) {
		this.templateFile = templateFile;
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
