/**
 * 
 */
package com.isaacbrodsky.freeze2.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class contains signed and ZZT related writing routines. The unsigned
 * name is kept for no reason.
 * 
 * @author isaac
 * 
 */
public class UnsignedDataOutputStream extends DataOutputStream {
	private int count;
	
	/**
	 * @param out
	 */
	public UnsignedDataOutputStream(OutputStream out) {
		super(out);
		
		count = 0;
	}
	
	public void write(int b) throws IOException {
		super.write(b);
		count++;
	}

	/**
	 * Writes <code>len</code> bytes of zero padding.
	 * 
	 * @param len
	 */
	public void writeZeroPadding(int len) throws IOException {
		for (int i = 0; i < len; i++)
			out.write(0);
	}

	/**
	 * @param s
	 * @param forceLen
	 * @throws IOException
	 */
	public void writeZZTString(String s, int forceLen) throws IOException {
		if (s == null)
			s = "";
		
		out.write(s.length());

		if (s.length() > forceLen)
			throw new IOException("Can't write string; too long");

		out.write(s.getBytes());

		if (s.length() < forceLen)
			writeZeroPadding(forceLen - s.length());
	}

	/**
	 * This is an unsigned method.
	 * 
	 * @param value
	 */
	public void writeLEShort(int value) throws IOException {
		byte low = (byte) (value & 0x00FF);
		byte high = (byte) ((value & 0xFF00) >> 8);

		out.write(low);
		out.write(high);
	}

	/**
	 * This is a signed method.
	 * 
	 * @param value
	 */
	public void writeSignedLEShort(int value) throws IOException {
		byte low = (byte) value;
		byte high = (byte) (value >> 8);

		out.write(low);
		out.write(high);
	}

	/**
	 * @return
	 */
	public int getNumBytesWritten() {
		return count;
	}
}
