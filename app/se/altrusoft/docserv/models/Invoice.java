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

	private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static String[] FIELDS_TO_TRANSLATE = new String[] {};

	private Date invoiceDate;
	private Integer invoiceSeries;
	private Integer invoiceNumber;
	private Integer customerNumber;
	private String assignement;
	private String ourReference;
	private String customerReference;
	public String description;
	public List<String> consultants;
	private Date startDate;
	private Date endDate;
	@JsonIgnore
	private String currency;
	@JsonIgnore
	private Float VAT;
	@JsonIgnore
	private Float priceExcludingVAT;
	@JsonIgnore
	private Float priceIncludingVAT;

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
				currency = invoiceElement.getCurrency();
				priceExcludingVAT += invoiceElement.getPrice();
			}
		}

		// TODO: Handle VAT properly
		VAT = priceExcludingVAT * 0.25F;
		priceIncludingVAT = priceExcludingVAT + VAT;

	}

	public String getInvoiceDatePP() {
		return (invoiceDate != null) ? DATE_FORMAT.format(invoiceDate) : "-";
	}

	public String getStartDatePP() {
		return (startDate != null) ? DATE_FORMAT.format(startDate) : "-";
	}

	public String getEndDatePP() {
		return (endDate != null) ? DATE_FORMAT.format(endDate) : "-";
	}

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

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Integer getInvoiceSeries() {
		return invoiceSeries;
	}

	public void setInvoiceSeries(Integer invoiceSeries) {
		this.invoiceSeries = invoiceSeries;
	}

	public Integer getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(Integer invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Integer getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(Integer customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getAssignement() {
		return assignement;
	}

	public void setAssignement(String assignement) {
		this.assignement = assignement;
	}

	public String getOurReference() {
		return ourReference;
	}

	public void setOurReference(String ourReference) {
		this.ourReference = ourReference;
	}

	public String getCustomerReference() {
		return customerReference;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getConsultants() {
		return consultants;
	}

	public void setConsultants(List<String> consultants) {
		this.consultants = consultants;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<InvoiceElement> getInvoiceElements() {
		return invoiceElements;
	}

	public void setInvoiceElements(List<InvoiceElement> invoiceElements) {
		this.invoiceElements = invoiceElements;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Float getVAT() {
		return VAT;
	}

	public void setVAT(Float vAT) {
		VAT = vAT;
	}

	public Float getPriceExcludingVAT() {
		return priceExcludingVAT;
	}

	public void setPriceExcludingVAT(Float priceExcludingVAT) {
		this.priceExcludingVAT = priceExcludingVAT;
	}

	public Float getPriceIncludingVAT() {
		return priceIncludingVAT;
	}

	public void setPriceIncludingVAT(Float priceIncludingVAT) {
		this.priceIncludingVAT = priceIncludingVAT;
	}

}
