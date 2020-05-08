/**
 *
 */
package com.isaacbrodsky.freeze2.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Creates a buffer that can be used as a {@link OutputStream}. It's sink is a
 * byte buffer (which can be accessed as a String).
 *
 * <p>
 * It turns out that Java provides {@link java.io.ByteArrayOutputStream}. I did
 * not know that when I wrote this. Oh well.
 *
 * @author isaac
 */
public class StringBackedOutputStream extends OutputStream {
	private byte[] dat;
	private int ptr;

	/**
	 * Size that should be added to the buffer if the end is reached.
	 */
	private int adtl;

	/**
	 * Creates a new StringBackedOutputStream with the default buffer size.
	 * Enabled reallocation if the end of the buffer is reached.
	 */
	public StringBackedOutputStream() {
		this(1024, 1024); // nice round number
	}

	/**
	 * Creates a new StringBackedOutputStream with the specified buffer size.
	 * Enabled reallocation if the end of the buffer is reached.
	 */
	public StringBackedOutputStream(int sz) {
		this(sz, sz);
	}

	/**
	 * @param sz
	 *            Initial size of the buffer. Must be >0.
	 * @param adtl
	 *            Size the buffer should be increased by (by allocating a new
	 *            buffer and copying the old to the new) if the current buffer
	 *            is full.
	 *
	 *            <p>
	 *            <code>adtl</code> may be 0, in which case an
	 *            {@link IOException} will be thrown if the end of the buffer is
	 *            reached. Negative values are illegal.
	 */
	public StringBackedOutputStream(int sz, int adtl) {
		if (sz < 1)
			throw new IllegalArgumentException("Can't have <1 length buffer.");
		if (adtl < 0)
			throw new IllegalArgumentException(
					"Illegal value for additional buffer size. (<0)");
		dat = new byte[sz];
		ptr = 0;

		this.adtl = adtl;
	}

	/**
	 * Returns the length of valid data in the buffer. The field
	 * <code>getData().length</code> should not be used to obtain the length of
	 * valid data in the buffer. Indices beyond the length reported by this
	 * method (if they exist) are invalid and may contain garbage values.
	 *
	 * @return
	 */
	public int getLength() {
		return ptr + 1;
	}

	/**
	 * Returns a copy of the buffer.
	 *
	 * @return
	 */
	public byte[] getData() {
		return Arrays.copyOf(dat, dat.length);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		if (ptr >= dat.length) {
			if (adtl < 0)
				throw new IOException(
						"End of buffer and reallocation disabled.");

			dat = Arrays.copyOf(dat, dat.length + adtl);
		}
		dat[ptr] = (byte) b;
		ptr++;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// return "StringBackedOutputStream {ptr=" + ptr + ", adtl=" + adtl
		// + ", &sz=" + dat.length + "}";
		return new String(getData(), 0, ptr + 1);
	}

	/**
	 * @param i
	 */
	public void truncateTo(int i) {
		dat = Arrays.copyOf(dat, i);
	}
}
