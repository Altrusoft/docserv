/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SpreadsheetModel extends TemplateModel {

	private static String[] FIELDS_TO_TRANSLATE = new String[] {};

	private String year;

	private String info;

	private List<SpreadRow> rows = new ArrayList<>();

	private SpreadRow total;

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

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public List<SpreadRow> getRows() {
		return rows;
	}

	public void setRows(List<SpreadRow> rows) {
		this.rows = rows;
	}

	public SpreadRow getTotal() {
		return total;
	}

	public void setTotal(SpreadRow total) {
		this.total = total;
	}

}
