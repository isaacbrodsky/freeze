/**
 * 
 */
package com.isaacbrodsky.freeze.filehandling;

import java.io.IOException;

import com.isaacbrodsky.freeze.game.GameController;

/**
 * @author isaac
 * 
 */
public interface Loader {
	public GameController load(WorldList base, String file) throws IOException;
}
