/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SpreadsheetModel extends TemplateModel {

	private static String[] FIELDS_TO_TRANSLATE = new String[] {};

	@Override
	public String getInTemplateDesignation() {
		return "sheet";
	}

	@Override
	public String[] fieldsToTranslate() {
		return FIELDS_TO_TRANSLATE;
	}

	@Override
	public void expandModel() {

	}

	public String year;

	public String info;

	public List<SpreadRow> rows;

	public SpreadRow total;

}
