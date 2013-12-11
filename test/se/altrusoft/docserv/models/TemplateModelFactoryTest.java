/* 
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.models;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.LinkedHashMap;

import org.junit.Test;

public class TemplateModelFactoryTest {

	@Test
	public void shouldOnlyDetectEntityFields() throws Exception {
		LinkedHashMap<String, Object> templateValues = new LinkedHashMap<String, Object>();

		LinkedHashMap<String, Object> lecturer = new LinkedHashMap<String, Object>();
		lecturer.put("firstName", "Joe");
		lecturer.put("lastName", "Public");
		lecturer.put("age", 45);
		Date now = new Date();
		now.setTime(1198908717056L);
		lecturer.put("now", now);

		LinkedHashMap<String, Object> university = new LinkedHashMap<String, Object>();
		university.put("name", "MIT");

		templateValues.put("university", university);
		templateValues.put("lecturer", lecturer);

		TemplateModel templateModel = DynamicModel.getTemplateModel(
				"MyDynamicClass", templateValues);
		// assert(5, templateModel.getClass().getDeclaredFields().length);
		assertNotNull(templateModel.getClass().getDeclaredField(
				"$cglib_prop_firstName"));

		try {
			templateModel.getField("noSuchField");
			fail("For unknown field, should have thrown NoSuchFieldException");
		} catch (NoSuchFieldException nsfe) {
			// expected exception
		}

		try {
			templateModel.getField("$cglib_prop_lecturer");
			fail("For non-entity field lecturer, should have thrown NoSuchFieldException");
		} catch (NoSuchFieldException nsfe) {
			// expected exception
		}
	}

	@Test
	public void shouldFailIfNotValidMap() {
		LinkedHashMap<String, Object> templateValues = new LinkedHashMap<String, Object>();
		Object something = null;
		templateValues.put("something", something);

		try {
			DynamicModel
					.getTemplateModel("MyNeverCreatedClass", templateValues);
			fail("Shouldn't have been able to create class based on invalid map");
		} catch (Exception e) {
			// expected exception
		}
	}
}
