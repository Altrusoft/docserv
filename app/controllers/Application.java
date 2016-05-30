/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.opensagres.xdocreport.core.XDocReportException;
import play.Configuration;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.BodyParser.Json;
import play.mvc.Controller;
import play.mvc.Result;
import se.altrusoft.docserv.converter.DocumentConversionException;
import se.altrusoft.docserv.converter.DocumentConverter;
import se.altrusoft.docserv.converter.MimeType;
import se.altrusoft.docserv.converter.UnsuportedConversionException;
import se.altrusoft.docserv.converter.libreoffice.LibreOfficeDocumentConverter;
import se.altrusoft.docserv.models.DynamicModel;
import se.altrusoft.docserv.models.TemplateModel;
import se.altrusoft.docserv.models.TemplateModelFactory;

public class Application extends Controller {

	private static final String ENCODING_BASE64 = "base64";

	public static final TemplateModelFactory templateModelFactory = new TemplateModelFactory();

	// TODO: Inject this.
	public static final DocumentConverter documentConverter = new LibreOfficeDocumentConverter();
	
	
	public static Result index() {
		String version = Configuration.root().getString("app.version");
		Collection<PropertiesConfiguration> templateConfigs = templateModelFactory.getTemplateConfigs();
		return ok(views.html.index.render(version, templateConfigs));
	}

	@BodyParser.Of(Json.class)
	public static Result getDocument(String templateName, String encoding) {

		Logger.debug("Recived request to generate " + templateName + " with encoding " + encoding);

		ObjectMapper mapper = new ObjectMapper();

		String json = request().body().asJson().toString();

		String acceptHeader = request().getHeader(ACCEPT);

		Optional<TemplateModel> requestedTemplateModel = templateModelFactory.getTemplateModel(templateName);

		if (requestedTemplateModel.isPresent()) {
			Logger.debug("Found requested template model " + templateName);
		} else {
			String errorMessage = "Unknown template: " + templateName;
			Logger.error(errorMessage);
			return badRequest(errorMessage);
		}

		TemplateModel templateModel = requestedTemplateModel.get();

		try {
			if (templateModel.isDynamic()) {
				Logger.debug("Template model {} is dynamic...", templateName);
				Object readValue = mapper.readValue(json, Object.class);

				@SuppressWarnings("unchecked")
				LinkedHashMap<String, Object> jsonValue = (LinkedHashMap<String, Object>) readValue;
				TemplateModel dynamicTemplateModel = DynamicModel.getTemplateModel(templateName, jsonValue);

				// TODO: applicationContext properties has to be injected in
				// the new dynamically generated model ...
				// This is only partially done here for the template file -
				// needs design?
				dynamicTemplateModel.setTemplateFile(templateModel.getTemplateFile());
				templateModel = dynamicTemplateModel;
			} else {
				mapper.readerForUpdating(templateModel).readValue(json);
				templateModel.translateProperties();
				templateModel.expandModel();
			}
		} catch (JsonParseException e) {
			String errorMessage = "Unable to parse received JSON data";
			Logger.error(errorMessage, e);
			return badRequest(errorMessage);
		} catch (JsonMappingException e) {
			String errorMessage = "Received JSON data does not map to template model";
			Logger.error(errorMessage, e);
			return badRequest(errorMessage);
		} catch (IOException e) {
			String errorMessage = "IOException while reading Json data";
			Logger.error(errorMessage, e);
			return internalServerError(errorMessage);
		} catch (java.lang.Exception e) {
			String errorMessage = "Unable to create model from JSON data";
			Logger.error(errorMessage, e);
			return internalServerError(errorMessage);
		}

		MimeType mimeType = MimeType.getMimeType(acceptHeader);
		ByteArrayOutputStream generatedDocumentOutputStream = null;

		try {
			if (mimeType != null) {
				generatedDocumentOutputStream = templateModel.generateDocument();

				if (mimeType.getConvertFilterName() != null) {
					Logger.debug("Converting generating document to " + mimeType.getValue() + "...");
					generatedDocumentOutputStream = documentConverter.convert(generatedDocumentOutputStream, mimeType);
				}
			} else {
				String warnMessage = "Unsupported mime-type: " + acceptHeader;
				Logger.warn(warnMessage);
				return badRequest(warnMessage);
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
		} catch (DocumentConversionException e) {
			String errorMessage = "Unexpected error when converting document";
			Logger.error(errorMessage, e);
			e.printStackTrace();
		} catch (UnsuportedConversionException e) {
			String errorMessage = "Unsuported output format";
			Logger.error(errorMessage, e);
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(generatedDocumentOutputStream);
		}

		if (encoding != null && encoding.equalsIgnoreCase(ENCODING_BASE64)) {
			// TODO: Do this in parallel, i.e. do not produce the ByteArray...
			byte[] content = generatedDocumentOutputStream.toByteArray();
			byte[] bs64EncodedContent = new Base64().encode(content);
			Logger.debug("Returning base 64 encoded content OK");
			response().setHeader(CONTENT_TRANSFER_ENCODING, ENCODING_BASE64);
			return ok(new ByteArrayInputStream(bs64EncodedContent)).as(mimeType.getValue());
		} else {
			Logger.debug("Returning OK");
			return ok(new ByteArrayInputStream(generatedDocumentOutputStream.toByteArray())).as(mimeType.getValue());
		}
	}

}
