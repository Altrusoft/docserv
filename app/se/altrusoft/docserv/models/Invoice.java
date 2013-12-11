/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Invoice extends TemplateModel {

	private static String[] FIELDS_TO_TRANSLATE = new String[] {};

	@Override
	public String getInTemplateDesignation() {
		return "inv";
	}

	@Override
	public String[] fieldsToTranslate() {
		return FIELDS_TO_TRANSLATE;
	}

	@Override
	public void expandModel() {

		currency = "";
		VAT = 0F;
		priceExcludingVAT = 0F;
		priceIncludingVAT = 0F;

		if (invoiceElements != null) {
			for (InvoiceElement invoiceElement : invoiceElements) {
				// TODO: Handle many currencies -- HH
				currency = invoiceElement.currency;
				priceExcludingVAT += invoiceElement.getPrice();
			}
		}

		// TODO: Handle VAT properly
		VAT = priceExcludingVAT * 0.25F;
		priceIncludingVAT = priceExcludingVAT + VAT;

	}

	private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	public String getInvoiceDatePP() {
		return (invoiceDate != null) ? formatter.format(invoiceDate) : "-";
	}

	public Date invoiceDate;
	public Integer invoiceSeries;
	public Integer invoiceNumber;
	public Integer customerNumber;
	public String assignement;
	public String ourReference;
	public String customerReference;

	public String description;
	public List<String> consultants;

	public String getStartDatePP() {
		return (startDate != null) ? formatter.format(startDate) : "-";
	}

	public Date startDate;

	public String getEndDatePP() {
		return (endDate != null) ? formatter.format(endDate) : "-";
	}

	public Date endDate;

	public List<InvoiceElement> invoiceElements;

	public String getPriceExcludingVATPP() {
		return currency + " " + priceExcludingVAT;
	}

	public String getVATPP() {
		return currency + " " + VAT;
	}

	public String getPriceIncludingVATPP() {
		return currency + " " + priceIncludingVAT;
	}

	@JsonIgnore
	public String currency;
	@JsonIgnore
	public Float VAT;
	@JsonIgnore
	public Float priceExcludingVAT;
	@JsonIgnore
	public Float priceIncludingVAT;

}
