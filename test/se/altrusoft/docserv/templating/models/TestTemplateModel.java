/*
 * Copyright (c) 2013 Altrusoft AB. This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain
 * one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.templating.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import se.altrusoft.docserv.models.TemplateModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestTemplateModel extends TemplateModel {


	private static String[] FIELDS_TO_TRANSLATE = new String[] {};


	private String testString = "Hoppsan";

	private String testUrl = "http://www.klassiker.nu/sites/klassiker.nu/files/styles/main_image_full/public/images/2013/07/saab1.jpg?itok=7OHLWZ7M";

	private List<TestTemplateComponent> components = new ArrayList<TestTemplateComponent>(
		Arrays.asList(new TestTemplateComponent("a1"), new TestTemplateComponent("a2")));



	@Override
	public String getInTemplateDesignation() {
		return "model";
	}

	@Override
	public String[] fieldsToTranslate() {
		return FIELDS_TO_TRANSLATE;
	}

	@Override
	public void expandModel() {



	}

	public String getTestString() {
		return testString;
	}

	public void setTestString(String testString) {
		this.testString = testString;
	}

	public String getTestUrl() {
		return testUrl;
	}

	public void setTestUrl(String testUrl) {
		this.testUrl = testUrl;
	}

	public List<TestTemplateComponent> getComponents() {
		return components;
	}

	public void setComponents(List<TestTemplateComponent> components) {
		this.components = components;
	}


}
