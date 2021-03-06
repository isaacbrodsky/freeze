/**
 * 
 */
package com.isaacbrodsky.freeze.utils;

/**
 * @author isaac
 * 
 */
public final class TimeAndMathUtils {

	private TimeAndMathUtils() {

	}

	/**
	 * Round a double to the specified number of decimal places.
	 */
	public static double roundPlaces(double num, int places) {
		double result = num * (Math.pow(10, places));
		result = Math.round(result);
		result = result / Math.pow(10, places);
		return result;
	}

	/**
	 * @param cmd
	 * @return
	 * @throws NumberFormatException
	 */
	public static int parseInt(String cmd) throws NumberFormatException {
		cmd = cmd.toLowerCase();
		int radix = 10;
		if (cmd.startsWith("x")) {
			radix = 16;
			cmd = cmd.substring(1);
		}
		return Integer.parseInt(cmd, radix);
	}

	/**
	 * @param i
	 * @param places
	 * @param pad
	 * @return
	 */
	public static String padInt(int i, int places, char pad) {
		// I knew I solved this before!
		// From com.isaacbrodsky.game.editor.Cursor
		return String.format("%" + pad + places + "d", i);
		// StringBuilder sb = new StringBuilder(Integer.toString(i));
		//
		// while (sb.length() < places) {
		// sb.insert(0, pad);
		// }
		//
		// return sb.toString();
	}

	/**
	 * @param i
	 * @param iddeclen
	 * @param c
	 * @param j
	 * @return
	 */
	public static String padInt(int i, int places, char pad, int radix) {
		StringBuilder sb = new StringBuilder(Integer.toString(i, radix));
		
		while (sb.length() < places)
			sb.insert(0, pad);
		
		return sb.toString();
	}

	/**
	 * Retrieves the "ones column" from the given int.
	 * 
	 * @param val
	 * @return
	 */
	public static int getOnes(int val) {
		// probably inefficient
		// how can this be done without looping?
		// val & 1, val & 2, val &3 ?
		// while (val > 10) {
		// val -= 10;
		// }
		//
		// return val;

		for (int i = 9; i > 0; i--) {
			if ((val & i) == i) {
				return i;
			}
		}

		return 0;
	}
}
