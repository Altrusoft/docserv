/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.controllers;

public enum MimeType {
	PDF("application/pdf", "writer_pdf_Export"), ODS(
			"application/vnd.oasis.opendocument.spreadsheet"), ODT(
			"application/vnd.oasis.opendocument.text"), XLS(
			"application/vnd.ms-excel", "MS Excel 97");
	private String value;
	private String convertFilterName;

	MimeType(String value) {
		this.value = value;
	}

	MimeType(String value, String convertFilterName) {
		this.value = value;
		this.convertFilterName = convertFilterName;
	}

	public String getValue() {
		return value;
	}

	public String getConvertFilterName() {
		return convertFilterName;
	}

	public static MimeType getMimeType(String mimeType) {
		for (MimeType enumMimeType : MimeType.values()) {
			if (enumMimeType.getValue().equalsIgnoreCase(mimeType)) {
				return enumMimeType;
			}
		}

		return null;
	}
}
