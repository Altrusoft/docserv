/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;

import org.apache.commons.beanutils.BeanUtils;

public class DynamicModel extends TemplateModel {

	public static TemplateModel getTemplateModel(String templateName,
			LinkedHashMap<String, Object> templateValues) throws Exception {
		Class<?> dynamicBeanClass = getDynamicBeanClass(templateName,
				templateValues);
		Object dynamicBeanObject = dynamicBeanClass.newInstance();
		BeanUtils.populate(dynamicBeanObject, templateValues);

		if (dynamicBeanObject instanceof TemplateModel) {
			return (TemplateModel) dynamicBeanObject;
		}

		throw new Exception(
				"Unexpectly failed to generate DynamicBeanObject based on template values");
	}

	private static Class<?> getDynamicBeanClass(String templateName,
			LinkedHashMap<String, Object> model) {
		Map<String, Class<?>> classAttributes = new HashMap<String, Class<?>>();
		traverseStructure(model, classAttributes);
		// TODO: probably have different names for each usage of same template
		// (the first json-object might not be complete containing all possible
		// parameters)
		return createBeanClass(templateName, TemplateModel.class,
				classAttributes);
	}

	@SuppressWarnings("unchecked")
	private static void traverseStructure(Object node,
			Map<String, Class<?>> attributes) {
		if (node instanceof Map) {
			LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) node;
			Set<Entry<String, Object>> entrySet = map.entrySet();
			Iterator<Entry<String, Object>> iterator = entrySet.iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				traverseStructure(entry, attributes);
			}
		} else if (node instanceof Entry) {
			Entry<String, Object> entry = (Entry<String, Object>) node;
			Object value = entry.getValue();
			if (value instanceof Map) {
				traverseStructure(value, attributes);
			} else {
				Class<?> attributeClass = value.getClass();
				String attributeName = entry.getKey();
				attributes.put(attributeName, attributeClass);
			}
		}
	}

	private static Class<?> createBeanClass(final String className,
			Class<TemplateModel> superClass, final Map<String, Class<?>> properties) {
		final BeanGenerator beanGenerator = new BeanGenerator();
		beanGenerator.setSuperclass(superClass);
		beanGenerator.setNamingPolicy(new NamingPolicy() {
			@Override
			public String getClassName(final String prefix,
					final String source, final Object key, final Predicate names) {
				return className;
			}
		});
		BeanGenerator.addProperties(beanGenerator, properties);
		return (Class<?>) beanGenerator.createClass();
	}

	@Override
	public boolean isDynamic() {
		return true;
	}
}
