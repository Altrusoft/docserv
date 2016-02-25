/*
 * Copyright (c) 2016 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.converter;

public class DocumentConversionException extends Exception {

	private static final long serialVersionUID = -5170559818200700487L;

	public DocumentConversionException() {
		super();
	}

	public DocumentConversionException(String message) {
		super(message);
	}

	public DocumentConversionException(String message, Throwable cause) {
		super(message, cause);
	}

	public DocumentConversionException(Throwable cause) {
		super(cause);
	}
}