/**
 * 
 */
package com.isaacbrodsky.freeze2.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Allows a String, or any series of bytes, to be used as an input stream
 * 
 * 
 * <p>
 * It turns out that Java provides {@link java.io.ByteArrayInputStream}. I did
 * not know that when I wrote this. Oh well.
 * 
 * @author isaac
 */
public class StringBackedInputStream extends InputStream {
	private byte[] data;
	private int index;
	private int mark;
	private boolean unsign;

	public StringBackedInputStream(InputStream is) throws IOException {
		this(is, true);
	}

	public StringBackedInputStream(InputStream is, boolean unsign) throws IOException {
		this.index = 0;
		this.mark = 0;
		this.unsign = unsign;

		StringBackedOutputStream dump = new StringBackedOutputStream();
		int count = 0;
		int temp = is.read();
		while (temp != -1) {
			dump.write(temp);
			
			count++;
			temp = is.read();
		}
		
		dump.truncateTo(count);
		this.data = dump.getData();
		
		is.close();
	}
	
	/**
	 * @param s
	 */
	public StringBackedInputStream(String s) {
		this(s.getBytes(), true);
	}

	/**
	 * @param s
	 */
	public StringBackedInputStream(byte[] s) {
		this(s, true);
	}

	/**
	 * @param s
	 */
	public StringBackedInputStream(String s, boolean unsign) {
		this(s.getBytes(), unsign);
	}

	/**
	 * @param s
	 */
	public StringBackedInputStream(byte[] s, boolean unsign) {
		this.data = s;
		this.index = 0;
		this.mark = 0;
		this.unsign = unsign;
	}

	/**
	 * @param b
	 * @param unsigned
	 */
	public StringBackedInputStream(int[] b, boolean unsigned) {
		data = new byte[b.length];
		System.arraycopy(b, 0, data, 0, b.length);
		this.index = 0;
		this.mark = 0;
		this.unsign = unsigned;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		if (index == data.length) {
			// throw new EOFException();
			// whoops that's not the documented functionality
			return -1; // "EOF"
		}
		byte b = data[index];
		int i = b;
		if (unsign) {
			i = (int) b & 0xFF;
		}
		index++;
		if (unsign)
			return i;
		return b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#mark(int)
	 */
	@Override
	public synchronized void mark(int readlimit) {
		mark = index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#reset()
	 */
	@Override
	public synchronized void reset() throws IOException {
		index = mark;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#markSupported()
	 */
	@Override
	public boolean markSupported() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StringBackedInputStream {index=" + index + ", mark=" + mark
				+ ", &sz=" + data.length + ", unsigned=" + unsign + "}";
	}

}
