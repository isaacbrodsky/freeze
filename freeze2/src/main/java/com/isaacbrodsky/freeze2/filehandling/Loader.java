/**
 * 
 */
package com.isaacbrodsky.freeze2.filehandling;

import com.isaacbrodsky.freeze2.game.GameController;

import java.io.IOException;

/**
 * @author isaac
 * 
 */
public interface Loader {
	GameController load(WorldList base, String file) throws IOException;
}
