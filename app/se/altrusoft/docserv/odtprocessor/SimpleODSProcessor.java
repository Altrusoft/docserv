/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.odtprocessor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SimpleODSProcessor implements DOMTransformer {
	private static final String TABLE_CELL = "table:table-cell";
	private static final String TABLE_FORMULA = "table:formula";
	private static final String OFFICE_VALUE = "office:value";
	private static final String OFFICE_VALUE_TYPE = "office:value-type";

	@Override
	public void transform(Document document) {
		NodeList cells = document.getElementsByTagName(TABLE_CELL);
		Element cell = null;

		for (int i = 0; i < cells.getLength(); i++) {
			cell = (Element) cells.item(i);
			String valueType = cell.getAttribute(OFFICE_VALUE_TYPE);
			if (valueType.equals("string")) {
				NodeList textNodes = cell.getElementsByTagName("text:p");
				if (textNodes.getLength() > 0) {
					Node textNode = textNodes.item(0).getFirstChild();

					if (textNode != null) {

						String textValue = textNode.getNodeValue();

						if (textValue != null) {

							// Is it a formula?
							if (textValue.startsWith("=")) {
								cell.setAttribute(OFFICE_VALUE_TYPE, "float");
								cell.setAttribute(TABLE_FORMULA, "of:"
										+ textValue);
							}

							// Is it a float?
							try {
								// Try to parse the value as a float. If it
								// isn't a float, an exception is thrown
								Double.parseDouble(textValue);
								// There is a float in a
								// string cell ...
								// change it
								// to a float
								cell.setAttribute(OFFICE_VALUE_TYPE, "float");
								cell.setAttribute(OFFICE_VALUE, textValue);
							} catch (NumberFormatException e) {
								// It is not a float in
								// the
								// cell ... keep
								// unchanged
							}
						}

					}
				}
			}
		}
	}
}
