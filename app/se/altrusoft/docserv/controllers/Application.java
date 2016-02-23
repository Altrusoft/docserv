/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.uno.Exception;
import com.sun.star.uno.XComponentContext;

import fr.opensagres.xdocreport.core.XDocReportException;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.BodyParser.Json;
import play.mvc.Controller;
import play.mvc.Result;
import se.altrusoft.docserv.controllers.ooconverter.OOoInputStream;
import se.altrusoft.docserv.controllers.ooconverter.OOoOutputStream;
import se.altrusoft.docserv.controllers.ooconverter.OOoStreamConverter;
import se.altrusoft.docserv.models.DynamicModel;
import se.altrusoft.docserv.models.TemplateModel;
import se.altrusoft.docserv.models.TemplateModelFactory;


public class Application extends Controller {
	private static final String ENCODING_BASE64 = "base64";

	public static final  TemplateModelFactory templateModelFactory = new TemplateModelFactory();


	@SuppressWarnings("unchecked")
	@BodyParser.Of(Json.class)
	public static Result getDocument(String templateName, String encoding) {
		// TODO: Use one static ObjectMapper of re-creating?
		ObjectMapper mapper = new ObjectMapper();
		String json = request().body().asJson().toString();
		String acceptHeader = request().getHeader(ACCEPT);

		TemplateModel templateModel = templateModelFactory.getTemplateModel(templateName);
		//templates.get(templateName).getClone();

		try {
			if (!templateModel.isDynamic()) {
				mapper.readerForUpdating(templateModel).readValue(json);

				templateModel.translateProperties();

				/* Expand model - if needed -HH */
				templateModel.expandModel();
			} else {
				Object readValue = mapper.readValue(json, Object.class);
				LinkedHashMap<String, Object> jsonValue = (LinkedHashMap<String, Object>) readValue;
				TemplateModel dynamicTemplateModel = DynamicModel
						.getTemplateModel(templateName, jsonValue);
				// TODO: applicationContext properties has to injected in
				// the new dynamically generated model ...
				// This is only partially done here for the template file -
				// needs design?
				dynamicTemplateModel.setTemplateFile(templateModel
						.getTemplateFile());
				templateModel = dynamicTemplateModel;
			}
		} catch (JsonParseException e) {
			Logger.warn("Unable to parse received Json data - bad request", e);
			return badRequest("JsonParseException");
		} catch (JsonMappingException e) {
			Logger.warn(
					"Received Json data does not map to template model - bad resquest",
					e);
			return badRequest("Received Json data does not map to template model");
		} catch (IOException e) {
			Logger.error("IOException while reading Json data", e);
			return internalServerError("IOException while reading Json data");
		} catch (java.lang.Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ByteArrayOutputStream generatedDocumentOutputStream = null;

		try {
			MimeType mimeType = MimeType.getMimeType(acceptHeader);

			if (mimeType != null) {
				generatedDocumentOutputStream=templateModel.generateDocument();

				if (mimeType.getConvertFilterName() != null) {
					Logger.debug("Generating " + mimeType.getValue() + "...");

					generatedDocumentOutputStream = convert(
							generatedDocumentOutputStream,
							mimeType.getConvertFilterName(),
							mimeType.getFilterParameters());
				}
			} else {
				String warnMessage = "Unsupported mime-type: " + acceptHeader;
				Logger.warn(warnMessage);
				return badRequest(warnMessage);
			}

			// TODO: Do this in parallel, i.e. do not produce the ByteArray...
			// --HH
			if (encoding != null && encoding.equalsIgnoreCase(ENCODING_BASE64)) {
				byte[] content = generatedDocumentOutputStream.toByteArray();
				byte[] bs64EncodedContent = new Base64().encode(content);
				Logger.info("Returning base 64 encoded content OK");
				response()
						.setHeader(CONTENT_TRANSFER_ENCODING, ENCODING_BASE64);
				return ok(new ByteArrayInputStream(bs64EncodedContent)).as(
						mimeType.getValue());
			} else {
				Logger.info("Returning OK");
				return ok(
						new ByteArrayInputStream(generatedDocumentOutputStream
								.toByteArray())).as(mimeType.getValue());
			}
		} catch (XDocReportException e) {
			String errorMessage = "Unable to generate document";
			Logger.error(errorMessage, e);
			e.printStackTrace();
			return internalServerError(errorMessage);
		} catch (IOException e) {
			String errorMessage = "Unexpected error when generating document";
			Logger.error(errorMessage, e);
			e.printStackTrace();
			return internalServerError(errorMessage);
		} catch (BootstrapException e) {
			String errorMessage = "Unable to bootstrap LibreOffice";
			Logger.error(errorMessage, e);
			e.printStackTrace();
			return internalServerError(errorMessage);
		} catch (Exception e) {
			String errorMessage = "LibreOffice Exception";
			Logger.error(errorMessage, e);
			e.printStackTrace();
			return internalServerError(errorMessage);
		} finally {
			IOUtils.closeQuietly(generatedDocumentOutputStream);
		}
	}

	private static ByteArrayOutputStream convert(
			ByteArrayOutputStream odxStream, String targetFormat,
			Map<String, Object> filterParameters) throws BootstrapException,
			Exception, IOException {
		// TODO: Can this be done once during app init?
		XComponentContext xContext = Bootstrap.bootstrap();

		OOoStreamConverter converter = new OOoStreamConverter(xContext);

		ByteArrayOutputStream generatedPDFOutputStream = new ByteArrayOutputStream();
		OOoOutputStream convertedOutputStream = null;
		OOoInputStream generatedODFInputStream = null;
		try {
			convertedOutputStream = null;
			generatedODFInputStream = new OOoInputStream(
					odxStream.toByteArray());
			convertedOutputStream = new OOoOutputStream();
			converter.convert(generatedODFInputStream, convertedOutputStream,
					targetFormat, filterParameters);

			generatedPDFOutputStream.write(convertedOutputStream.toByteArray());
		} finally {
			IOUtils.closeQuietly(generatedODFInputStream);
			IOUtils.closeQuietly(convertedOutputStream);
		}

		return generatedPDFOutputStream;
	}
	
}
