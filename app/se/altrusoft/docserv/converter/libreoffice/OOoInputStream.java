/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.converter.libreoffice;

import java.io.ByteArrayInputStream;

import com.sun.star.io.BufferSizeExceededException;
import com.sun.star.io.NotConnectedException;
import com.sun.star.io.XInputStream;
import com.sun.star.io.XSeekable;

/**
 * <a href="http://www.oooforum.org/forum/viewtopic.phtml?t=13205">OOInputStream
 * from the thread <b>OOo-Java: Using XInputStream...</b></a>
 */
public class OOoInputStream extends ByteArrayInputStream implements
		XInputStream, XSeekable {

	public OOoInputStream(byte[] buf) {
		super(buf);
	}

	//
	// Implement XInputStream
	//

	@Override
	public int readBytes(byte[][] buffer, int bufferSize)
			throws NotConnectedException, BufferSizeExceededException,
			com.sun.star.io.IOException {
		int numberOfReadBytes;
		try {
			byte[] bytes = new byte[bufferSize];
			numberOfReadBytes = super.read(bytes);
			if (numberOfReadBytes > 0) {
				if (numberOfReadBytes < bufferSize) {
					byte[] smallerBuffer = new byte[numberOfReadBytes];
					System.arraycopy(bytes, 0, smallerBuffer, 0,
							numberOfReadBytes);
					bytes = smallerBuffer;
				}
			} else {
				bytes = new byte[0];
				numberOfReadBytes = 0;
			}

			buffer[0] = bytes;
			return numberOfReadBytes;
		} catch (java.io.IOException e) {
			throw new com.sun.star.io.IOException(e.getMessage(), this);
		}
	}

	@Override
	public int readSomeBytes(byte[][] buffer, int bufferSize)
			throws NotConnectedException, BufferSizeExceededException,
			com.sun.star.io.IOException {
		return readBytes(buffer, bufferSize);
	}

	@Override
	public void skipBytes(int skipLength) throws NotConnectedException,
			BufferSizeExceededException, com.sun.star.io.IOException {
		skip(skipLength);
	}

	@Override
	public void closeInput() throws NotConnectedException,
			com.sun.star.io.IOException {
		try {
			close();
		} catch (java.io.IOException e) {
			throw new com.sun.star.io.IOException(e.getMessage(), this);
		}
	}

	//
	// Implement XSeekable
	//

	@Override
	public long getLength() throws com.sun.star.io.IOException {
		return count;
	}

	@Override
	public long getPosition() throws com.sun.star.io.IOException {
		return pos;
	}

	@Override
	public void seek(long position) throws IllegalArgumentException,
			com.sun.star.io.IOException {
		pos = (int) position;
	}
}
