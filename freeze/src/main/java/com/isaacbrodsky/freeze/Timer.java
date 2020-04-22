/**
 * 
 */
package com.isaacbrodsky.freeze;

/**
 * @author isaac
 * 
 */
public class Timer {
	/**
	 * This number may not be <=0
	 */
	private double timeStep;

	public Timer() {
		timeStep = 100;
	}

	public Timer(double n) {
		setTimeStep(n);
	}

	/**
	 * Time in milliseconds.
	 * 
	 * Equivalent to <code>getCurrTime() - start</code>
	 */
	public long timeSince(long start) {
		return getCurrTime() - start;
	}

	/**
	 * Time in milliseconds.
	 * 
	 * Equivalent to <code>timeSince(start) / increment</code>
	 */
	public long incrementsSince(long start, long increment) {
		return timeSince(start) / increment;
	}

	/**
	 * Use this instead of <code>System.currentTimeMillis()</code>
	 * 
	 * @return
	 */
	public long getCurrTime() {
		return System.currentTimeMillis();
	}

	/**
	 * @return
	 */
	public double getTimeStep() {
		return timeStep;
	}

	public void setTimeStep(double n) {
		if (n <= 0)
			throw new IllegalArgumentException("bad time step (<= 0)");
		timeStep = n;
	}
}
