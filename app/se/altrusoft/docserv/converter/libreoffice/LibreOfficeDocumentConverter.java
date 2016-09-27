/*
 * Copyright (c) 2016 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.converter.libreoffice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.uno.Exception;
import com.sun.star.uno.XComponentContext;

import se.altrusoft.docserv.converter.DocumentConversionException;
import se.altrusoft.docserv.converter.DocumentConverter;
import se.altrusoft.docserv.converter.MimeType;
import se.altrusoft.docserv.converter.UnsuportedConversionException;

public class LibreOfficeDocumentConverter implements DocumentConverter {

	@Override
	public ByteArrayOutputStream convert(byte[] inDocument, MimeType targetMime)
			throws DocumentConversionException, UnsuportedConversionException {

		// TODO: Currently which conversions that are supported is governed by
		// MimeType (correct?)
		String convertFilterName = targetMime.getConvertFilterName();
		Map<String, Object> filterParameters = targetMime.getFilterParameters();

		XComponentContext xContext;
		try {
			xContext = Bootstrap.bootstrap();
		} catch (BootstrapException e) {
			throw new DocumentConversionException("Unable to bootstrap LibreOffice", e);
		}

		OOoStreamConverter converter = new OOoStreamConverter(xContext);

		ByteArrayOutputStream generatedPDFOutputStream = new ByteArrayOutputStream();
		OOoOutputStream convertedOutputStream = null;
		OOoInputStream generatedODFInputStream = null;
		try {
			convertedOutputStream = null;
			generatedODFInputStream = new OOoInputStream(inDocument);
			convertedOutputStream = new OOoOutputStream();
			converter.convert(generatedODFInputStream, convertedOutputStream, convertFilterName, filterParameters);

			generatedPDFOutputStream.write(convertedOutputStream.toByteArray());
		} catch (Exception e) {
			throw new DocumentConversionException("LibreOffice Exception", e);
		} catch (IOException e) {
			throw new DocumentConversionException("Unbale to write converted document to output stream", e);
		} finally {
			IOUtils.closeQuietly(generatedODFInputStream);
			IOUtils.closeQuietly(convertedOutputStream);
		}

		return generatedPDFOutputStream;
	}

}
