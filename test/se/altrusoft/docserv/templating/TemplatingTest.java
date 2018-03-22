/*
 * Copyright (c) 2013 Altrusoft AB. This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain
 * one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.templating;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static play.test.Helpers.fakeApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Optional;

import org.junit.Test;

import net.sf.corn.cps.ResourceFilter;
import play.Logger;
import play.test.FakeApplication;
import se.altrusoft.docserv.models.TemplateModel;
import se.altrusoft.docserv.models.TemplateModelFactory;
import se.altrusoft.docserv.odtprocessor.ImageUrlODTProcessor;

public class TemplatingTest {

	@Test
	public void test_template_model_loaded() {
		try {
			ResourceFilter resourceFilter = new ResourceFilter().packageName("public.templates.test.*");
			TemplateModelFactory templateModelFactory = new TemplateModelFactory(resourceFilter);// "se.altrusoft.docserv.templating.templates.*");
			assertTrue("Factory loaded", templateModelFactory != null);
			Optional<TemplateModel> requestedTemplateModel = templateModelFactory.getTemplateModel("testTemplate"); // "se.altrusoft.docserv.templating.TestTemplateModel");
			assertTrue("Template model loaded", requestedTemplateModel.isPresent());
			TemplateModel templateModel = requestedTemplateModel.get();
			templateModel.setPreProcessor(new ImageUrlODTProcessor());
			Logger.info("Template file name: " + templateModel.getTemplateFileName());
			FakeApplication application = fakeApplication();
			InputStream in = application.resourceAsStream(templateModel.getTemplateFileName());
			ByteArrayOutputStream generatedDocumentOutputStream = templateModel.generateDocument(application);
			byte[] output = generatedDocumentOutputStream.toByteArray();
			File outputFile = new File(
				"target/test-classes/out/se/altrusoft/docserv/odsprocessor/test_template_output.odt");
			outputFile.getParentFile().mkdirs();
			FileOutputStream outputFileSteam = new FileOutputStream(outputFile);
			outputFileSteam.write(output);
			outputFileSteam.flush();
			outputFileSteam.close();
		} catch (Exception e) {
			Logger.error(e.getMessage());
			fail("Error");
			e.printStackTrace();
		}
	}


	// @Test
	// public void imageUrlODTProcessorShouldWork() {
	// try {
	// String imageSpecifcation = "{ \"url\" :
	// \"http://www.altrusoft.se/wp-content/uploads/2017/04/etik.png\" }";
	// ODTProcessor
	// .transformODT(
	// "test/se/altrusoft/docserv/odsprocessor/image_document.odt",
	// "target/test-classes/out/se/altrusoft/docserv/odsprocessor/image_document_output.ods",
	// new ImageUrlODTProcessor(imageSpecifcation));
	// } catch (FileNotFoundException e) {
	// fail("No such file");
	// e.printStackTrace();
	// }
	// }
}
