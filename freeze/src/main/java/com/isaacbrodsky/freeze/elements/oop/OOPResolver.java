/**
 * 
 */
package com.isaacbrodsky.freeze.elements.oop;

import com.isaacbrodsky.freeze.elements.data.Stats;

/**
 * @author isaac
 * 
 */
public final class OOPResolver {
	private OOPResolver() {
		// Utility class
	}

	/**
	 * Returns <code>true</code> if the given character is considered a
	 * "control character". A control character is one which indicates the given
	 * line contains OOP code rather than text for display to the user. (Special
	 * text formatting characters such as '$' and '!' are not considered control
	 * characters.)
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isControl(char c) {
		switch (c) {
		case '@':
		case '#':
		case ':':
		case '\'':
		case '/':
		case '?':
			return true;

		default:
			return false;
		}
	}

	/**
	 * Used in new
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isTextControl(char c) {
		switch (c) {
		case '$':
		case '!':
			return true;

		default:
			return false;
		}
	}

	public static String readLineAt(String oop, int loc) {
		StringBuilder sb = new StringBuilder();

		if (oop.length() <= loc)
			return null;
		char c = oop.charAt(loc);
		while (c != '\r') {
			sb.append(c);
			loc++;
			if (oop.length() <= loc)
				break;
			c = oop.charAt(loc);
		}
		// loc++; // \n after \r
		// sb.append(oop.charAt(loc));

		return sb.toString();
	}

	/**
	 * @param oop
	 * @param i
	 * @return
	 */
	public static String readText(String oop, int loc) {
		StringBuilder sb = new StringBuilder();

		boolean first = true;
		if (oop.length() <= loc)
			return "";
		while (true) {
			String s = readLineAt(oop, loc);
			if (s == null)
				break;

			if (s.length() > 0)
				if (isControl(s.charAt(0)))
					break;

			if (first)
				first = false;
			else {
				sb.append('\n');
			}
			sb.append(s);
			loc += s.length() + 1;
		}

		return sb.append('\r').toString();
	}

	/**
	 * @param oop
	 * @param i
	 * @return
	 */
	public static String readToControl(String oop, int loc) {
		StringBuilder sb = new StringBuilder();

		if (oop.length() <= loc)
			return "";
		char c = oop.charAt(loc);
		while (!(isControl(c) || c == '\r')) {
			sb.append(c);
			loc++;
			if (oop.length() <= loc)
				break;
			c = oop.charAt(loc);
		}
		if (c == '\r')
			sb.append(c);

		return sb.toString();
	}

	/**
	 * Used in new
	 * 
	 * @param s
	 * @return
	 */
	public static String readToControl(Stats s) {
		StringBuilder sb = new StringBuilder();
		String oop = s.getOop();
		int loc = s.currInstr;

		if (oop.length() <= loc)
			return "";
		char c = oop.charAt(loc);
		while (!(isControl(c) || c == '\r')) {
			sb.append(c);
			loc++;
			if (oop.length() <= loc)
				break;
			c = oop.charAt(loc);
		}
		if (c == '\r')
			sb.append(c);

		s.currInstr = loc;

		return sb.toString();
	}

	/**
	 * @param o
	 * @return
	 */
	public static boolean isLabel(OOPInstruction o) {
		if (o.getData().startsWith(":"))
			return true;
		else
			return false;
	}

	/**
	 * @param o
	 * @return
	 */
	public static boolean isComment(OOPInstruction o) {
		if (o.getData().startsWith("'"))
			return true;
		else
			return false;
	}

	/**
	 * @param o
	 * @return
	 */
	public static String labelText(OOPInstruction o) {
		return o.getData().substring(1).trim();
	}

	/**
	 * @param cmd
	 * @return
	 */
	public static String[] toParts(String cmd) {
		String[] s = cmd.split(" ");
		String[] s2 = new String[s.length];
		int pos = 0;
		for (int i = 0; i < s.length; i++) {
			if (!s[i].trim().equals("")) {
				s2[pos] = s[i].trim();
				pos++;
			}
		}
		String[] s3 = new String[pos];
		System.arraycopy(s2, 0, s3, 0, pos);
		return s3;
	}
}
