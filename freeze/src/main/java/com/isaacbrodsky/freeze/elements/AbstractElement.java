/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import com.isaacbrodsky.freeze.elements.data.Stats;

/**
 * @author isaac
 * 
 */
public abstract class AbstractElement implements Element {
	private int x, y;
	private Stats s = null;

	public int getCycle() {
		if (s != null)
			return s.getCycle();
		else
			return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getStats()
	 */
	@Override
	public Stats getStats() {
		if (s != null) {
			s.x = getX();
			s.y = getY();
			return s;
		} else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.elements.Element#setStats(com.isaacbrodsky.
	 * freeze.elements.data.Stats)
	 */
	@Override
	public void setStats(Stats stats) {
		s = stats;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#setXY(int, int)
	 */
	@Override
	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getX()
	 */
	@Override
	public int getX() {
		return x;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getY()
	 */
	@Override
	public int getY() {
		return y;
	}
}
