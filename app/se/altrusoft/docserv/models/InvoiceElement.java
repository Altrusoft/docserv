/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceElement {

	private String specification;
	private Float numberOfUnits;
	private String unitSort;
	private Float unitPrice;
	private String currency;
	private String perUnitSort;

	public String getPricePP() {
		return currency + " " + getPrice();
	}

	public Float getPrice() {
		return numberOfUnits * unitPrice;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public Float getNumberOfUnits() {
		return numberOfUnits;
	}

	public void setNumberOfUnits(Float numberOfUnits) {
		this.numberOfUnits = numberOfUnits;
	}

	public String getUnitSort() {
		return unitSort;
	}

	public void setUnitSort(String unitSort) {
		this.unitSort = unitSort;
	}

	public Float getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Float unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getPerUnitSort() {
		return perUnitSort;
	}

	public void setPerUnitSort(String perUnitSort) {
		this.perUnitSort = perUnitSort;
	}

}
