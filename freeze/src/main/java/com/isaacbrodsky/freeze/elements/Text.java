/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.graphics.ElementColoring;

/**
 * @author isaac
 * 
 */
public class Text extends Solid {
	private ElementColoring color;
	private int charIndex;

	public void createInstance(SaveData dat) {
		super.createInstance(dat);

		charIndex = dat.getColor();

		switch (dat.getType()) {
		case 47: // BLUE
		case 73:
			color = new ElementColoring("WHITE", "DARKBLUE",
					ElementColoring.ColorMode.RECESSIVE);
			break;
		case 48: // GREEN
		case 74:
			color = new ElementColoring("WHITE", "DARKGREEN",
					ElementColoring.ColorMode.RECESSIVE);
			break;
		case 49: // CYAN
		case 75:
			color = new ElementColoring("WHITE", "DARKCYAN",
					ElementColoring.ColorMode.RECESSIVE);
			break;
		case 50: // RED
		case 76:
			color = new ElementColoring("WHITE", "DARKRED",
					ElementColoring.ColorMode.RECESSIVE);
			break;
		case 51: // PURPLE
		case 77:
			color = new ElementColoring("WHITE", "PURPLE",
					ElementColoring.ColorMode.RECESSIVE);
			break;
		case 52: // BROWN
		case 78:
			color = new ElementColoring("WHITE", "BROWN",
					ElementColoring.ColorMode.RECESSIVE);
			break;
		case 53: // WHITE
		case 79:
			color = new ElementColoring("WHITE", "BLACK",
					ElementColoring.ColorMode.RECESSIVE);
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Solid#getColoring()
	 */
	public ElementColoring getColoring() {
		return color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Solid#getDisplayCharacter()
	 */
	public int getDisplayCharacter() {
		return charIndex;
	}
}
