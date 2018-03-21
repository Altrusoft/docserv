/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.odsprocessor;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
//import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;
import se.altrusoft.docserv.models.immutable.ImageSpecification;
/*
* <draw:frame draw:style-name="fr1" draw:name="Bild1" text:anchor-type="paragraph" svg:width="17cm" svg:height="7.719cm" draw:z-index="0">
* 	<draw:image xlink:href="http://www.altrusoft.se/wp-content/uploads/2017/04/kaernvaerden.png" xlink:type="simple" xlink:show="embed" xlink:actuate="onLoad" draw:filter-name="&lt;Alla format&gt;"/>
* </draw:frame>
* 
*/
public class ImageUrlODTProcessor implements DOMTransformer {
	
	private ImageSpecification imageSpecification = null;
	
	public ImageUrlODTProcessor(String imageSpecification) {
		JsonNode json = Json.parse(imageSpecification);
		this.imageSpecification = Json.fromJson(json, ImageSpecification.class);
	}
	
	@Override
	public void transform(Document domdoc) {
		NodeList imageElements = domdoc.getElementsByTagName("draw:image");
		Element imageElement = null;


		for (int i = 0; i < imageElements.getLength(); i++) {
			imageElement = (Element) imageElements.item(i);
			Attr urlAttr = imageElement.getAttributeNode("xlink:href");
			String attributeValue = urlAttr.getValue();
			System.out.println("URL image link: "+ attributeValue);
			imageElement.setAttribute("xlink:href", imageSpecification.url);
		}
	}
}
