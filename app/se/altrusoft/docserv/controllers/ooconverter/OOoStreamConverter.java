/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.controllers.ooconverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.star.beans.PropertyValue;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.XCloseable;

/**
 * Based on OOoStreamConverter from the thread <a
 * href="http://forum.openoffice.org/en/forum/viewtopic.php?t=3801">[Java
 * solution] Using XInputStream and XOutputStream</a>
 * 
 */
public class OOoStreamConverter {
	private XComponentContext xComponentContext;

	public OOoStreamConverter(XComponentContext xComponentContext) {
		this.xComponentContext = xComponentContext;
	}

	public void convert(OOoInputStream input, OOoOutputStream output,
			String filterName, Map<String, Object> filterParameters)
			throws Exception {
		XMultiComponentFactory xMultiComponentFactory = xComponentContext
				.getServiceManager();
		Object desktopService = xMultiComponentFactory
				.createInstanceWithContext("com.sun.star.frame.Desktop",
						xComponentContext);
		XComponentLoader xComponentLoader = UnoRuntime.queryInterface(
				XComponentLoader.class, desktopService);

		PropertyValue[] conversionProperties = new PropertyValue[3];
		conversionProperties[0] = new PropertyValue();
		conversionProperties[1] = new PropertyValue();
		conversionProperties[2] = new PropertyValue();

		conversionProperties[0].Name = "InputStream";
		conversionProperties[0].Value = input;
		conversionProperties[1].Name = "Hidden";
		conversionProperties[1].Value = Boolean.TRUE;

		XComponent document = xComponentLoader.loadComponentFromURL(
				"private:stream", "_blank", 0, conversionProperties);

		List<PropertyValue> filterData = new ArrayList<PropertyValue>();
		for (Map.Entry<String, Object> entry : filterParameters.entrySet()) {
			PropertyValue propertyValue = new PropertyValue();
			propertyValue.Name = entry.getKey();
			propertyValue.Value = entry.getValue();
			filterData.add(propertyValue);
		}

		conversionProperties[0].Name = "OutputStream";
		conversionProperties[0].Value = output;
		conversionProperties[1].Name = "FilterName";
		conversionProperties[1].Value = filterName;
		conversionProperties[2].Name = "FilterData";
		conversionProperties[2].Value = filterData
				.toArray(new PropertyValue[1]);

		XStorable xstorable = UnoRuntime.queryInterface(XStorable.class,
				document);
		xstorable.storeToURL("private:stream", conversionProperties);

		XCloseable xclosable = UnoRuntime.queryInterface(XCloseable.class,
				document);
		xclosable.close(true);
	}
}
