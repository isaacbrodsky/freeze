/**
 * 
 */
package com.isaacbrodsky.freeze.elements.oop;

/**
 * @author isaac
 * 
 */
public final class OOPInstruction {
	private final int loc, endLoc;
	private final String data;

	public OOPInstruction(final int loc, final int endLoc, final String data) {
		this.loc = loc;
		this.endLoc = endLoc;
		this.data = data;
	}

	/**
	 * Returns the location of the first byte of this instruction in its script.
	 * 
	 * @return
	 */
	public int getLoc() {
		return loc;
	}

	/**
	 * Returns the location of the last byte of this instruction in its script.
	 * 
	 * @return
	 */
	public int getEndLoc() {
		return endLoc;
	}

	public String getData() {
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return loc + " -> " + endLoc + " : " + data;
	}
}
