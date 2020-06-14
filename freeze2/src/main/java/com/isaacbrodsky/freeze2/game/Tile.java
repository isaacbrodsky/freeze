/**
 * 
 */
package com.isaacbrodsky.freeze2.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.isaacbrodsky.freeze2.elements.CommonElements;
import com.isaacbrodsky.freeze2.elements.Element;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;

import java.util.Objects;

/**
 */
public final class Tile {
	/**
	 * Reference into ElementDefs
	 */
	private final int type;

	/**
	 * Color code
	 */
	private final int color;
	
	public Tile() {
		this(CommonElements.EMPTY, 0);
	}

	public Tile(Element type, int color) {
		this(type.code(), color);
	}

	public Tile(final int type, final ElementColoring color) {
		this(type, color.getCode());
	}

	/**
	 * @param type
	 * @param color
	 */
	@JsonCreator
	public Tile(
			@JsonProperty("type") final int type,
			@JsonProperty("color") final int color
	) {
		if (type < 0 || type > 0xFF)
			throw new IllegalArgumentException("Type code cannot be < 0 or > 0xFF");
		if (color < 0 || color > 0xFF)
			throw new IllegalArgumentException("Color code cannot be < 0 or > 0xFF");

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

	@Override
	public String toString() {
		return "type=" + type + ", color=" + ElementColoring.forCode(color);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Tile tile = (Tile) o;
		return type == tile.type &&
				color == tile.color;
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, color);
	}
}
