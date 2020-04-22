/**
 * 
 */
package com.isaacbrodsky.freeze.elements.data;

import com.isaacbrodsky.freeze.graphics.ElementColoring;

/**
 * SaveData encapsulates the save information needed for each element. It does
 * not include extended "Stat" information (see {@link Stats}).
 * 
 * <p>
 * This SaveData is not required to match the display characteristics of the
 * element generating it. Some elements, such as
 * {@link com.isaacbrodsky.freeze.elements.Text Text} use the type and color
 * fields differently than other elements. Some element such as
 * {@link com.isaacbrodsky.freeze.elements.Empty Empty} mask their "true"
 * color in gameplay but return the correct color in SaveData (to facilitate
 * <code>#put</code> in OOP)
 * 
 * <p>
 * Immutable.
 * 
 * @author isaac
 */
public final class SaveData {
	/**
	 * 
	 */
	private final int type;

	/**
	 * 
	 */
	private final int color;
	
	public SaveData() {
		this(0, 0);
	}

	public SaveData(final int type, final ElementColoring color) {
		this(type, color.getCode());
	}

	/**
	 * @param type
	 * @param color
	 */
	public SaveData(final int type, final int color) {
		// if (type <= 0)
		// throw new IllegalArgumentException("Type code cannot be < 0");

		this.type = type;
		this.color = color;
	}

	/**
	 * @return
	 */
	public final int getType() {
		return type;
	}

	/**
	 * @return
	 */
	public final int getColor() {
		return color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("type=").append(type).append(";color=").append(color);
		return sb.toString();
	}
}
