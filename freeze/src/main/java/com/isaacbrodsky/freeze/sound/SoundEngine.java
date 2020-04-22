/**
 * 
 */
package com.isaacbrodsky.freeze.sound;

/**
 * @author isaac
 * 
 */
public interface SoundEngine {
	/**
	 * @return
	 */
	public boolean init();

	/**
	 * 
	 */
	public void close();

	/**
	 * 
	 */
	public void start();

	/**
	 * @return
	 */
	public String getTune();

	/**
	 * @return
	 */
	public int getTunePos();

	/**
	 * @param t
	 */
	public void setTune(String t);

	/**
	 * @param p
	 */
	public void setTunePos(int p);
}
