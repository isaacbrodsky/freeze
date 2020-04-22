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
public class Fake extends Empty {
	private ElementColoring color;

	public Fake() {
	}

	@Override
	public void createInstance(SaveData dat) {
		super.createInstance(dat);
		this.color = new ElementColoring(dat.getColor(),
				ElementColoring.ColorMode.RECESSIVE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Empty#getColoring()
	 */
	@Override
	public ElementColoring getColoring() {
		return color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Empty#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		return 178;
	}
}
