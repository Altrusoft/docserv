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

	public String specification;
	public Float numberOfUnits;
	public String unitSort;
	public Float unitPrice;
	public String currency;
	public String perUnitSort;

	public String getPricePP() {
		return currency + " " + getPrice();
	}

	public Float getPrice() {
		return numberOfUnits * unitPrice;
	}
}
