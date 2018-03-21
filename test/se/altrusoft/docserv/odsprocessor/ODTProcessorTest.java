/* 
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.odsprocessor;

import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import org.junit.Test;

public class ODTProcessorTest {

	@Test
	public void test_markup() {
		try {
			ODTProcessor
					.transformODT(
							"test/se/altrusoft/docserv/odsprocessor/test_markup_input.ods",
							"target/test-classes/out/se/altrusoft/docserv/odsprocessor/test_markup_output.ods",
							new MarkupODSProcessor());
		} catch (FileNotFoundException e) {
			fail("No such file");
			e.printStackTrace();
		}
	}

	@Test
	public void simpleODSProcessorShouldWork() {
		try {
			ODTProcessor
					.transformODT(
							"test/se/altrusoft/docserv/odsprocessor/test_floatification_input.ods",
							"target/test-classes/out/se/altrusoft/docserv/odsprocessor/test_floatification_output.ods",
							new SimpleODSProcessor());
		} catch (FileNotFoundException e) {
			fail("No such file");
			e.printStackTrace();
		}
	}
	
	@Test
	public void imageUrlODTProcessorShouldWork() {
		try {
			String imageSpecifcation = "{ \"url\" : \"http://www.altrusoft.se/wp-content/uploads/2017/04/etik.png\" }";
			ODTProcessor
					.transformODT(
							"test/se/altrusoft/docserv/odsprocessor/image_document.odt",
							"target/test-classes/out/se/altrusoft/docserv/odsprocessor/image_document_output.ods",
							new ImageUrlODTProcessor(imageSpecifcation));
		} catch (FileNotFoundException e) {
			fail("No such file");
			e.printStackTrace();
		}
	}
}
