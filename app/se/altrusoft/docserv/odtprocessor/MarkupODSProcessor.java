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

public class MarkupODSProcessor implements DOMTransformer {
	@Override
	public void transform(Document domdoc) {
		NodeList cells = domdoc.getElementsByTagName("table:table-cell");
		Element cell = null;

		for (int i = 0; i < cells.getLength(); i++) {
			cell = (Element) cells.item(i);
			NodeList textNodes = cell.getElementsByTagName("text:p");
			if (textNodes.getLength() > 0) {
				Node textNode = textNodes.item(0).getFirstChild();
				String textValue = textNode.getNodeValue();
				String pattern = "(^@@float:)(.*)";
				if (textValue.matches(pattern)) {
					String newValue = textValue.replaceAll(pattern, "$2");

					String valueType = cell.getAttribute("office:value-type");
					String value = cell.getAttribute("office:value");
					System.out.println(valueType + ":" + value + "/"
							+ textValue);
					cell.setAttribute("office:value-type", "float");
					cell.setAttribute("office:value", newValue);
					textNode.setNodeValue(newValue);
				}
			}

		}
	}
}
