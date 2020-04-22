/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import com.isaacbrodsky.freeze.graphics.ElementColoring;

/**
 * This class specifies a generic implementation for "glitch" elements. The
 * following elements are implemented through this class: Messenger (#2), and
 * #46.
 * 
 * <p>
 * These two elements appear to have the same implementation: always appear
 * blank (0 for coloring and 0 for character) and do nothing, ever.
 * 
 * <p>
 * #46 is likely simply a glitch element which behaves as a Solid due to
 * defaults. I believe is may be possible #46 is somehow related to the text
 * elements that follow it numerically. The true purpose, history, and woof woof
 * of element Messenger in ZZT are unknown.
 * 
 * @author isaac
 */
public class GlitchElement extends Solid {
	private static final ElementColoring GLITCH_COLOR = new ElementColoring(0);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Solid#getColoring()
	 */
	@Override
	public ElementColoring getColoring() {
		return GLITCH_COLOR;
	}

	/**
	 * @author isaac
	 * 
	 */
	public static class Messenger extends GlitchElement {

	}

	/**
	 * @author isaac
	 * 
	 */
	public static class Element46 extends GlitchElement {

	}
}
