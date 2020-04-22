/**
 * 
 */
package com.isaacbrodsky.freeze.utils;

import java.io.*;

/**
 * This class contains signed and ZZT related reading routines. The unsigned
 * name is kept for no reason.
 * 
 * @author Owner
 * 
 */
public class UnsignedDataInputStream extends DataInputStream {

	/**
	 * @param in
	 */
	public UnsignedDataInputStream(InputStream in) {
		super(in);
	}

	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#read()
	 */
	@Override
	public int read() throws IOException {
		int i = super.read();
		
		//this is not technically correct but w/e
		if (i < 0)
			throw new EOFException();
		
		return i;
	}

	/**
	 * READS LITTLE-ENDIAN
	 * 
	 * @return
	 * @throws IOException
	 */
	public long readUnsignedLEInt() throws IOException {
		int buf[] = new int[4];

		buf[0] = read();
		buf[1] = read();
		buf[2] = read();
		buf[3] = read();

		return recreateUnsignedLEInt(buf);
		// if (ret > Integer.MAX_VALUE)
		// throw new IOException("Bad value");
	}

	public static long recreateUnsignedLEInt(int[] buf) {
		int firstByte = (0x000000FF & ((int) buf[0]));
		int secondByte = (0x000000FF & ((int) buf[1]));
		int thirdByte = (0x000000FF & ((int) buf[2]));
		int fourthByte = (0x000000FF & ((int) buf[3]));

		return ((long) (firstByte | secondByte << 8 | thirdByte << 16 | fourthByte << 24)) & 0xFFFFFFFFL;
	}

	/**
	 * READS BIG-ENDIAN
	 * 
	 * @return
	 * @throws IOException
	 */
	public long readUnsignedInt() throws IOException {
		int buf[] = new int[4];

		buf[0] = read();
		buf[1] = read();
		buf[2] = read();
		buf[3] = read();

		return recreateUnsignedInt(buf);
		// if (ret > Integer.MAX_VALUE)
		// throw new IOException("Bad value");
	}

	public static long recreateUnsignedInt(int[] buf) {
		int firstByte = (0x000000FF & ((int) buf[0]));
		int secondByte = (0x000000FF & ((int) buf[1]));
		int thirdByte = (0x000000FF & ((int) buf[2]));
		int fourthByte = (0x000000FF & ((int) buf[3]));

		return ((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;
	}

	public static int recreateUnsignedShort(int[] buf) {
		int firstByte = (0x000000FF & ((int) buf[0]));
		int secondByte = (0x000000FF & ((int) buf[1]));

		return (int) (((firstByte << 8 | secondByte)) & 0xFFFFFFFFL);
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public char readUnsignedLEShort() throws IOException {
		int buf[] = new int[2];

		buf[0] = read();
		buf[1] = read();

		int firstByte = (0x000000FF & ((int) buf[0]));
		int secondByte = (0x000000FF & ((int) buf[1]));

		char ret = ((char) (firstByte | secondByte << 8));
		// if (ret > Character.MAX_VALUE)
		// throw new IOException("Bad value");
		return ret;
	}

	/**
	 * @param i
	 * @param j
	 * @param k
	 * @param l
	 * @return
	 */
	public static long recreateUnsignedLEInt(int i, int j, int k, int l) {
		return recreateUnsignedLEInt(new int[] { i, j, k, l });
	}

	/**
	 * @param i
	 * @param j
	 * @param k
	 * @param l
	 * @return
	 */
	public static long recreateUnsignedInt(int i, int j, int k, int l) {
		return recreateUnsignedInt(new int[] { i, j, k, l });
	}

	public static int unsignByte(byte b) {
		return (0x000000FF & ((int) b));
	}

	/**
	 * @return
	 */
	public int readSignedLEInt() throws IOException {
		int buf[] = new int[4];
		buf[0] = read();
		buf[1] = read();
		buf[2] = read();
		buf[3] = read();

		return (int) (((int) (buf[0] | buf[1] << 8 | buf[2] << 16 | buf[3] << 24)) & 0xFFFFFFFFL);
	}

	/**
	 * ..THIS METHOD WAS FIXED..
	 * 
	 * @return
	 */
	public int readSignedLEShort() throws IOException {
		int buf[] = new int[2];
		buf[0] = read();
		buf[1] = read();

		int ret = (int) (((int) (buf[0] | buf[1] << 8)));
		if (ret >= 0x8000) {
			ret = -((0xFFFF ^ ret) + 1);
		}
		return ret;
	}

	public char readSingleChar() throws IOException {
		return (char) read();
	}

	public String readZZTString() throws IOException {
		return readZZTString(0);
	}

	public String readZZTString(int forceLen) throws IOException {
		int len = read();
		String s = readZZTStringFixedLength(Math.min(len, forceLen));
		if (len < forceLen)
			skip(forceLen - len);

		return s;
	}

	/**
	 * @param oopLength
	 * @return
	 * @throws IOException
	 */
	public String readZZTStringFixedLength(int len) throws IOException {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append(readSingleChar());
		}
		return sb.toString();
	}
}
