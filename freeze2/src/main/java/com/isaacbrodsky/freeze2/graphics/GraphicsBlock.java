/**
 * 
 */
package com.isaacbrodsky.freeze2.graphics;

/**
 * @author isaac
 * 
 */
public final class GraphicsBlock {
	private final ElementColoring color;
	private final int charIndex;

	/**
	 * Creates a black/0x00 block.
	 */
	public GraphicsBlock() {
		color = new ElementColoring(0);
		charIndex = 0;
	}

	public GraphicsBlock(final ElementColoring c, final int cI) {
		this.color = c;
		this.charIndex = cI;
	}

	public GraphicsBlock(final int c, final int cI) {
		this.color = ElementColoring.forCode(c);
		this.charIndex = cI;
	}

	public final ElementColoring getColoring() {
		return color;
	}

	public final int getCharIndex() {
		return charIndex;
	}
}
