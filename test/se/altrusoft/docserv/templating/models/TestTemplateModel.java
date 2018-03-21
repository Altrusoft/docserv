/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.templating.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import se.altrusoft.docserv.models.TemplateModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestTemplateModel extends TemplateModel {


	private static String[] FIELDS_TO_TRANSLATE = new String[] {};


	private String testString;

//	@Override
//	public String getInTemplateDesignation() {
//		return "inv";
//	}

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

	
}
