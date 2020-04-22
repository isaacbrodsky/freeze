/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

/**
 * @author isaac
 * 
 */
public class BlinkWallRay extends Solid {

	private BlinkWallRay() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Solid#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		if (this instanceof HorizontalRay)// getSaveData().getType() == 33
			return 205; // horiz
		else
			// 43
			return 186; // verti
	}

	public static class HorizontalRay extends BlinkWallRay {
		public HorizontalRay() {

		}
	}

	public static class VerticalRay extends BlinkWallRay {
		public VerticalRay() {

		}
	}
}
