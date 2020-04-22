/**
 * 
 */
package com.isaacbrodsky.freeze.elements.superz;

/**
 * Do not use SuperWater directly, use one of its subclasses.
 * 
 * @author isaac
 */
public class SuperWater extends Floor {
	private SuperWater() {
		// prevent instantiation
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.elements.superz.Floor#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		if (getClass().equals(WaterE.class))
			return 16;
		if (getClass().equals(WaterW.class))
			return 17;
		if (getClass().equals(WaterS.class))
			return 31;
		if (getClass().equals(WaterN.class))
			return 30;
		return 178;
	}

	public int getMoveX() {
		if (getClass().equals(WaterE.class))
			return 1;
		if (getClass().equals(WaterW.class))
			return -1;
		return 0;
	}

	public int getMoveY() {
		if (getClass().equals(WaterS.class))
			return 1;
		if (getClass().equals(WaterN.class))
			return -1;
		return 0;

	}

	public static class WaterE extends SuperWater {
		public WaterE() {

		}
	}

	public static class WaterN extends SuperWater {
		public WaterN() {

		}
	}

	public static class WaterS extends SuperWater {
		public WaterS() {

		}
	}

	public static class WaterW extends SuperWater {
		public WaterW() {

		}
	}
}
