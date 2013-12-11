/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.controllers.ooconverter;

import java.io.ByteArrayOutputStream;

import com.sun.star.io.BufferSizeExceededException;
import com.sun.star.io.NotConnectedException;
import com.sun.star.io.XOutputStream;

/**
 * <a href="http://www.oooforum.org/forum/viewtopic.phtml?t=13205">OOInputStream
 * from the thread <b>OOo-Java: Using XInputStream...</b></a>
 */
public class OOoOutputStream extends ByteArrayOutputStream implements
		XOutputStream {

	public OOoOutputStream() {
		super(32768);
	}

	//
	// Implement XOutputStream
	//

	@Override
	public void writeBytes(byte[] values) throws NotConnectedException,
			BufferSizeExceededException, com.sun.star.io.IOException {
		try {
			this.write(values);
		} catch (java.io.IOException e) {
			throw (new com.sun.star.io.IOException(e.getMessage()));
		}
	}

	@Override
	public void closeOutput() throws NotConnectedException,
			BufferSizeExceededException, com.sun.star.io.IOException {
		try {
			super.flush();
			super.close();
		} catch (java.io.IOException e) {
			throw (new com.sun.star.io.IOException(e.getMessage()));
		}
	}

	@Override
	public void flush() {
		try {
			super.flush();
		} catch (java.io.IOException e) {
			// do nothing
		}
	}
}
