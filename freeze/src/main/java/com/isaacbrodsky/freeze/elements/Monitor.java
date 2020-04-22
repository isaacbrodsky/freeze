/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import com.isaacbrodsky.freeze.graphics.ElementColoring;

/**
 * @author isaac
 * 
 */
public class Monitor extends Solid {
	private static final ElementColoring MONITOR_COLOR = new ElementColoring(0);

	@Override
	public ElementColoring getColoring() {
		return MONITOR_COLOR;
	}
}
