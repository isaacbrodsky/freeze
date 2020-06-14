/**
 * 
 */
package com.isaacbrodsky.freeze2.graphics;

import java.awt.Color;

/**
 * @author isaac
 *
 */
public final class ElementColoring {
	private final int code;
	private final NamedColor fore;
	private final NamedColor back;
	private final boolean blinking;

	protected ElementColoring(int x) {
		this.code = x;
		blinking = ((x & 0x80) == 0x80) ? true : false;
		x = ((x & 0x80) == 0x80) ? x ^ 0x80 : x; // clear blink flag
		int h = x >> 4;
		int l = (x & 0x0F);
		this.fore = NamedColor.colorFromCode(l);
		this.back = NamedColor.colorFromCode(h & 0x7F);
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
		return code & 0x0F;
	}

	/**
	 * Returns -1 if no such code could be found
	 * 
	 * @return
	 */
	public int getBackCode() {
		return (code >> 4) & 0x0F;
	}

	public int getCode() {
		return code;
	}

	public Color getFore() {
		return fore.color();
	}

	public Color getBack() {
		return back.color();
	}

	public NamedColor getForeName() { return fore; }

	public NamedColor getBackName() { return back; }

	public ElementColoring deriveBack(int b) {
		return ElementColoring.forCode((code & 0x0F) | (b << 4));
	}

	public ElementColoring deriveFore(int f) {
		return ElementColoring.forCode((code & 0xF0) | f);
	}

	@Override
	public String toString() {
		String additional = ((blinking) ? " blinking" : "");

		return fore + "/" + back + additional;
	}

	public ElementColoring toMonochrome() {
		if ((code & 0x08) == 0x08) {
			if ((code & 0xF0) == 0) {
				return forCode(0x0F);
			} else {
				return forCode(0x7F);
			}
		} else {
			if ((code & 0x07) != 0) {
				return forCode(0x07);
			} else {
				return forCode(0x70);
			}
		}
	}

	private final static ElementColoring[] STATIC_POOL = new ElementColoring[256];
	static {
		for (int i = 0; i < STATIC_POOL.length; i++) {
			STATIC_POOL[i] = new ElementColoring(i);
		}
	}

	public static ElementColoring forCode(int i) {
		return STATIC_POOL[i];
	}

	public static ElementColoring forNames(NamedColor fore, NamedColor back) {
		return forCode(fore.ordinal() | (back.ordinal() << 4));
	}

	public static ElementColoring forNames(NamedColor fore, NamedColor back, boolean blinking) {
		return forCode(fore.ordinal() | (back.ordinal() << 4) | (blinking ? 0x80 : 0));
	}
}
