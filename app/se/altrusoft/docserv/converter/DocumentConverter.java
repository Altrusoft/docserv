/*
 * Copyright (c) 2016 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.converter;

import java.io.ByteArrayOutputStream;

public interface DocumentConverter {

	public ByteArrayOutputStream convert(ByteArrayOutputStream documentInStream, MimeType targetType)
			throws DocumentConversionException, UnsuportedConversionException;

}
