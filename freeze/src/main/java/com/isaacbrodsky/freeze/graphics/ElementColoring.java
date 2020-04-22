/**
 * 
 */
package com.isaacbrodsky.freeze.graphics;

import java.awt.Color;

/**
 * @author isaac
 * 
 */
public final class ElementColoring {
	private final Color fore;
	private final Color back;
	private final ColorMode mode;
	private boolean blinking;

	public ElementColoring(final int f, final int b) {
		this(colorFromCode(f), colorFromCode(b), null);
	}

	public ElementColoring(final String f, final String b) {
		this(colorFromName(f), colorFromName(b), null);
	}

	public ElementColoring(final String f, final String b,
			final boolean blinking) {
		this(colorFromName(f), colorFromName(b), null);
		this.blinking = blinking;
	}

	public ElementColoring(final String f, final String b, final ColorMode m) {
		this(colorFromName(f), colorFromName(b), m);
	}

	public ElementColoring(final Color f, final Color b, final ColorMode m) {
		this.fore = f;
		this.back = b;
		this.mode = m;
		blinking = false;
	}

	/**
	 * @param i
	 */
	public ElementColoring(int i) {
		this(i, null);
	}

	public ElementColoring(int x, ColorMode m) {
		blinking = ((x & 0x80) == 0x80) ? true : false;
		x = ((x & 0x80) == 0x80) ? x ^ 0x80 : x; // clear blink flag
		int h = x >> 4;
		int l = (x & 0x0F);
		this.fore = colorFromCode(l);
		this.back = colorFromCode(h & 0x7F);
		this.mode = m;

		if (x != getCode())
			System.out.println(x + " =" + getCode() + " aaa " + toString());
	}

	public ElementColoring(int h, int l, ColorMode m) {
		this.fore = colorFromCode(l);
		this.back = colorFromCode(h);
		this.mode = m;
		blinking = false;
	}

	public boolean getBlinking() {
		return blinking;
	}

	/**
	 * Returns -1 if no such code could be found
	 * 
	 * @return
	 */
	public int getForeCode() {
		return codeFromColor(fore);
	}

	/**
	 * Returns -1 if no such code could be found
	 * 
	 * @return
	 */
	public int getBackCode() {
		return codeFromColor(back);
	}

	public int getCode() {
		int high = getBackCode() << 4;
		return getForeCode() | high;
	}

	public Color getFore() {
		return fore;
	}

	public Color getBack() {
		return back;
	}

	public ColorMode getMode() {
		return mode;
	}

	public ElementColoring deriveBack(int i) {
		return new ElementColoring(getForeCode(), i);
	}

	public ElementColoring deriveFore(int i) {
		return new ElementColoring(i, getBackCode());
	}

	public String toString() {
		String fstr = fore.getRed() + " " + fore.getGreen() + " "
				+ fore.getBlue();
		String bstr = back.getRed() + " " + back.getGreen() + " "
				+ back.getBlue();
		for (int i = 0; i < COLORS.length; i++) {
			if (COLORS[i].equals(fore))
				fstr = COLOR_NAMES[i];
			if (COLORS[i].equals(back))
				bstr = COLOR_NAMES[i];
		}

		String additional = ((mode != null) ? " " + mode : "")
				+ ((blinking) ? " blinking" : "");

		return fstr + "/" + bstr + additional;
	}

	public static enum ColorMode {
		DOMINANT, CODOMINANT, RECESSIVE
	}

	private static final Color[] COLORS = { new Color(0, 0, 0),
			new Color(0, 0, 168), new Color(0, 168, 0), new Color(0, 168, 168),
			new Color(168, 0, 0), new Color(168, 0, 168),
			new Color(168, 87, 0), new Color(168, 168, 168),
			new Color(87, 87, 87), new Color(87, 87, 255),
			new Color(87, 255, 87), new Color(87, 255, 255),
			new Color(255, 87, 87), new Color(255, 87, 255),
			new Color(255, 255, 87), new Color(255, 255, 255) };

	private static final String[] COLOR_NAMES = { "BLACK", "DARKBLUE",
			"DARKGREEN", "DARKCYAN", "DARKRED", "DARKPURPLE", "BROWN", "GRAY",
			"DARKGRAY", "BLUE", "GREEN", "CYAN", "RED", "PURPLE", "YELLOW",
			"WHITE" };

	/**
	 * Returns -1 if no such code exists
	 * 
	 * @param c
	 * @return
	 */
	public static int codeFromColor(Color c) {
		for (int i = 0; i < COLORS.length; i++) {
			if (COLORS[i].equals(c)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Returns <code>null</code> if no name could be found.
	 * 
	 * @param code
	 * @return
	 */
	public static String nameFromCode(int code) {
		if (COLOR_NAMES.length > code && code >= 0) {
			return COLOR_NAMES[code];
		}

		return null;
	}

	/**
	 * Returns <code>null</code> if no name could be found.
	 * 
	 * @param c
	 * @return
	 */
	public static String nameFromColor(Color c) {
		for (int i = 0; i < COLORS.length; i++) {
			if (COLORS[i].equals(c)) {
				return COLOR_NAMES[i];
			}
		}

		return null;
	}

	public static Color colorFromCode(int code) {
		if (code >= COLORS.length || code < 0)
			throw new ArrayIndexOutOfBoundsException("Invalid color code: "
					+ code);

		return COLORS[code];
	}

	public static int codeFromName(String name) {
		for (int i = 0; i < COLOR_NAMES.length; i++) {
			if (COLOR_NAMES[i].equalsIgnoreCase(name))
				return i;
		}

		return -1;
	}

	public static Color colorFromName(String name) {
		for (int i = 0; i < COLOR_NAMES.length; i++) {
			if (COLOR_NAMES[i].equalsIgnoreCase(name))
				return COLORS[i];
		}

		return null;
	}

	/**
	 * TODO
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static boolean matches(int c1, int c2) {
//TODO
		return false;
	}
}
