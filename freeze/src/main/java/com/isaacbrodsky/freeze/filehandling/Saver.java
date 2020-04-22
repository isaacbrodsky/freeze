/**
 * 
 */
package com.isaacbrodsky.freeze.filehandling;

import java.io.File;
import java.io.IOException;

import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.utils.UnsignedDataOutputStream;

/**
 * @author isaac
 * 
 */
public interface Saver {
	/**
	 * Must wrap to {@link #save(GameController, File)}
	 * 
	 * @param game
	 * @param s
	 * @return
	 * @throws IOException
	 */
	public void save(GameController game, WorldList base, String file) throws IOException;

	public void saveBoard(Board b, UnsignedDataOutputStream out)
			throws IOException;
}
