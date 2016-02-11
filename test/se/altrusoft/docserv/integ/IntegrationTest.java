/* 
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.integ;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.Arrays;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import play.Application;
import play.Play;
import play.libs.F.Callback;
import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.Response;
import play.libs.WS.WSRequestHolder;
import play.test.TestBrowser;
import se.altrusoft.docserv.controllers.MimeType;

/**
 * Test class for verifying the whole application.
 * 
 * Integration tests actually starting an instance of the server and then make
 * HTTP requests and verifies the output.
 * 
 */
public class IntegrationTest {

	private static final int DOCSERVER_PORT = 9000;
	private static final String DOCSERVER_BASE_URL = "http://localhost:"
			+ DOCSERVER_PORT;

	@Test
	public void goingToRootShouldShowPageWithListsOfRoutes() {
		running(testServer(DOCSERVER_PORT, fakeApplication()), HTMLUNIT,
				new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {

						browser.goTo(DOCSERVER_BASE_URL);
						String pageSource = browser.pageSource();
						Assert.assertTrue(pageSource
								.contains("Action not found"));
						Assert.assertFalse(pageSource.contains("Action found"));
					}
				});
	}

	@Test
	@Ignore("Fails ... TODO: Investigate")
	public void postOnODTTemplateURLWithJsonParamsAndAcceptingODT_ShouldGenerateFeededODT() {
		postOnTemplateURLWithJsonParams_ShouldGenerateFeededDocumentWithAcceptedMimeType(
				"invoiceOdt", MimeType.ODS.getValue(),
				"odt/expected_odt_invoice.odt");
	}

	@Test
	@Ignore("Fails ... TODO: Investigate")
	public void postOnODTTemplateWithoutClassURLWithJsonParamsAndAcceptingODT_ShouldGenerateFeededODT() {
		postOnTemplateURLWithJsonParams_ShouldGenerateFeededDocumentWithAcceptedMimeType(
				"invoiceNoClassOdt", MimeType.ODS.getValue(),
				"odt/expected_odt_invoice_no_class.odt");
	}

	@Test
	public void postOnODTTemplateURLWithJsonParamsAndAcceptingPDF_ShouldGenerateFeededPDF() {
		postOnTemplateURLWithJsonParams_ShouldGenerateFeededDocumentWithAcceptedMimeType(
				"invoiceOdt", MimeType.PDF.getValue(),
				"odt/expected_odt_invoice.pdf");
	}

	@Test
	public void postOnODSTemplateURLWithJsonParamsAndAcceptingODS_ShouldGenerateFeededODS() {
		postOnTemplateURLWithJsonParams_ShouldGenerateFeededDocumentWithAcceptedMimeType(
				"invoiceOds", MimeType.ODS.getValue(),
				"ods/expected_ods_invoice.ods");
	}

	@Test
	public void postOnODSTemplateURLWithJsonParamsAndAcceptingPDF_ShouldGenerateFeededPDF() {
		postOnTemplateURLWithJsonParams_ShouldGenerateFeededDocumentWithAcceptedMimeType(
				"invoiceOds", MimeType.PDF.getValue(),
				"ods/expected_ods_invoice.pdf");
	}

	@Test
	@Ignore("Fails on Hudson (old version of LibreOffice)")
	public void postOnODSTemplateURLWithJsonParamsAndAcceptingXLS_ShouldGenerateFeededXLS() {
		postOnTemplateURLWithJsonParams_ShouldGenerateFeededDocumentWithAcceptedMimeType(
				"invoiceOds", MimeType.XLS.getValue(),
				"ods/expected_ods_invoice.xls");
	}

	private void postOnTemplateURLWithJsonParams_ShouldGenerateFeededDocumentWithAcceptedMimeType(
			final String templateName, final String requestedMimeType,
			final String fileNameOfExpectedDocument) {

		running(testServer(DOCSERVER_PORT, fakeApplication()), HTMLUNIT,
				new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						String jsonParams = null;
						Application application = Play.application();
						String jsonParamFilePath = "test/json/test_invoice.json";
						try {
							File paramFile = application
									.getFile(jsonParamFilePath);
							jsonParams = FileUtils.readFileToString(paramFile);
						} catch (IOException e) {
							fail("Can't open file json param file \""
									+ jsonParamFilePath + "\": ("
									+ e.toString() + ")");
						}
						WSRequestHolder url = WS.url(DOCSERVER_BASE_URL
								+ "/document/" + templateName);
						url = url.setHeader("Content-Type", "application/json");
						url = url.setHeader("Accept", requestedMimeType);

						Promise<Response> response = url.post(jsonParams);
						assertEquals(200, response.get(1L, TimeUnit.MINUTES)
								.getStatus());

						File expectedFile = application.getFile("test/"
								+ fileNameOfExpectedDocument);
						assertResponseEqualsFileContent(expectedFile, response,
								true);
					}

				});
	}

	@Test
	public void requestingTemplateShouldReturnTemplate() {
		running(testServer(DOCSERVER_PORT, fakeApplication()), HTMLUNIT,
				new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						Application application = Play.application();

						WSRequestHolder url = WS.url(DOCSERVER_BASE_URL
								+ "/assets/templates/Invoice/template.odt");
						// TODO: strange that these headers do not matter
						// (default
						// values? then add test-cases)
						//
						// url = url.setHeader("Content-Type",
						// "application/json");
						// url = url.setHeader("Accept",
						// "application/vnd.oasis.opendocument.text");

						// url = url.setHeader("Accept",
						// "application/vnd.oasis.opendocument.text;charset=UTF-8");

						Promise<Response> response = url.get();
						assertEquals(200, response.get(1L, TimeUnit.MINUTES)
								.getStatus());

						File expectedFile = application
								.getFile("public/templates/Invoice/template.odt");
						assertResponseEqualsFileContent(expectedFile, response,
								false);
					}
				});
	}

	void assertResponseEqualsFileContent(File expectedFile,
			Promise<Response> response, boolean onlyVerifySize) {
		FileInputStream expectedInputStream = null;
		InputStream actualInputStream = null;
		try {
			expectedInputStream = FileUtils.openInputStream(expectedFile);
			byte[] expectedByteArray = IOUtils.toByteArray(expectedInputStream);

			actualInputStream = response.get().getBodyAsStream();
			byte[] actualByteArray = IOUtils.toByteArray(actualInputStream);

			// We only verify PDF files by raw text. This because we are
			// depending on external OpenOffice lib while
			// executing this test, and we can't be too pedantic about the
			// details then (different OO versions
			// generate slightly different files)
			if (expectedFile.getName().endsWith(".pdf")) {
				String expectedPageOne = getNonWhiteSpacesFromPDF(expectedByteArray);
				String actualPageOne = getNonWhiteSpacesFromPDF(actualByteArray);
				assertEquals("Not same text on first page", expectedPageOne,
						actualPageOne);
			} else {
				if (onlyVerifySize) {
					boolean hasGeneratedFileCorrectSize = (expectedByteArray.length == actualByteArray.length);
					if (!hasGeneratedFileCorrectSize) {
						fail("Actual file is not same size expected (exp size="
								+ new String(expectedByteArray).length()
								+ " act size="
								+ new String(actualByteArray).length());
					}
				} else {
					if (!Arrays.areEqual(expectedByteArray, actualByteArray)) {
						fail("Actual file has not same content as expected (exp size="
								+ new String(expectedByteArray).length()
								+ " act size="
								+ new String(actualByteArray).length());
					}
				}
			}
		} catch (IOException e) {
			fail("Can't compare generated file with expected file: "
					+ e.getMessage());
		} finally {
			IOUtils.closeQuietly(expectedInputStream);
			IOUtils.closeQuietly(actualInputStream);
		}
	}

	private String getNonWhiteSpacesFromPDF(byte[] pdfByteArray)
			throws IOException {
		String nonWhiteSpace;

		PdfReader pdfReader = null;
		try {
			pdfReader = new PdfReader(pdfByteArray);
			nonWhiteSpace = StringUtils.deleteWhitespace(PdfTextExtractor
					.getTextFromPage(pdfReader, 1));
			// remove non-break space
			nonWhiteSpace = nonWhiteSpace.replace("\u00A0", "");
            nonWhiteSpace = nonWhiteSpace.replace("\u00AD", "-");
		} catch (IOException e) {
			throw e;
		} finally {
			if (pdfReader != null) {
				pdfReader.close();
			}
		}

		return nonWhiteSpace;
	}
}
