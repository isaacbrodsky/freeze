/**
 * 
 */
package com.isaacbrodsky.freeze2.utils;

import java.util.Objects;

/**
 * @author isaac
 *
 */
public final class Point {
	private final int x, y;
	
	public Point(final int x, final int y) {
		this.x = x;
		this.y = y;
	}
	
	public final int getX() {
		return x;
	}
	
	public final int getY() {
		return y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Point point = (Point) o;
		return x == point.x &&
				y == point.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
}
