/**
 * 
 */
package com.isaacbrodsky.freeze.menus;

/**
 * @author isaac
 *
 */
public interface TypingInteraction extends UIInteraction {

	/**
	 * @param key
	 * @param mod
	 * @return
	 */
	public boolean keyTyped(int key, int mod);
}
