/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.odsprocessor;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ODSProcessor {

	public static final String CONTENT_FILE = "content.xml";

	public static void transformODS(String inputFileName,
			String outputFileName, DOMTransformer contentTransformer)
			throws FileNotFoundException {
		File inputFile = new File(inputFileName);
		File outputFile = new File(outputFileName);
		outputFile.getParentFile().mkdirs();
		
		transformODS(new FileInputStream(inputFile), new FileOutputStream(
				outputFile), contentTransformer);
	}

	public static void transformODS(InputStream inputStream,
			OutputStream outputStream, DOMTransformer contentTransformer) {
		ZipInputStream zipInputStream = null;
		ZipOutputStream zipOutputStream = null;
		try {
			zipInputStream = new ZipInputStream(new BufferedInputStream(
					inputStream));
			zipOutputStream = new ZipOutputStream(outputStream);

			for (ZipEntry zipInputEntry; (zipInputEntry = zipInputStream
					.getNextEntry()) != null;) {
				ZipEntry zipOutputEntry = null;
				InputStream zipEntrySource = null;

				String inputEntryName = zipInputEntry.getName();

				if (inputEntryName.equals(CONTENT_FILE)) {

					// Found Content File

					DocumentBuilderFactory dbFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

					// Build a DOM from the original content found in
					// 'zipInputStream'
					// There is a bug in dBuilder.parse() that will close
					// the input stream - this is why we have to extract all
					// content file data
					// from 'zipInputStream' in a temporary byte[] and wrap it
					// in a new input
					// stream...

					Document domdoc = dBuilder.parse(new ByteArrayInputStream(
							IOUtils.toByteArray(zipInputStream)));
					domdoc.getDocumentElement().normalize();

					// Perform transformation of DOM:
					contentTransformer.transform(domdoc);

					// Finally serialize the DOM now containing transformed
					// content.

					TransformerFactory transformerFactory = TransformerFactory
							.newInstance();
					Transformer transformer = transformerFactory
							.newTransformer();
					DOMSource domSource = new DOMSource(domdoc);

					ByteArrayOutputStream transformedContent = new ByteArrayOutputStream();
					StreamResult result = new StreamResult(transformedContent);
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
					transformer.transform(domSource, result);

					byte transformedContentBytes[] = transformedContent
							.toByteArray();
					zipOutputEntry = new ZipEntry(zipInputEntry.getName());
					zipOutputEntry.setSize(transformedContentBytes.length);
					zipEntrySource = new ByteArrayInputStream(
							transformedContentBytes);

				} else {
					zipOutputEntry = zipInputEntry;
					zipEntrySource = zipInputStream;
				}
				zipOutputStream.putNextEntry(zipOutputEntry);
				IOUtils.copy(zipEntrySource, zipOutputStream);
			}
		} catch (IOException e) {
			throw new Error("Unable to transform ODS", e);
		} catch (ParserConfigurationException e) {
			throw new Error("Unable to transform ODS", e);
		} catch (SAXException e) {
			throw new Error("Unable to transform ODS", e);
		} catch (TransformerConfigurationException e) {
			throw new Error("Unable to transform ODS", e);
		} catch (TransformerException e) {
			throw new Error("Unable to transform ODS", e);
		} finally {
			IOUtils.closeQuietly(zipInputStream);
			IOUtils.closeQuietly(zipOutputStream);
		}
	}
}
