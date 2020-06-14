/**
 * 
 */
package com.isaacbrodsky.freeze2.filehandling;

import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.utils.UnsignedDataOutputStream;

import java.io.IOException;

/**
 * @author isaac
 * 
 */
public interface Saver {
	void save(GameController game, WorldList base, String file) throws IOException;

	void saveBoard(Board b, UnsignedDataOutputStream out)
			throws IOException;
}
