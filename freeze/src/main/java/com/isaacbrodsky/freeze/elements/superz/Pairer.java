/**
 * 
 */
package com.isaacbrodsky.freeze.elements.superz;

import com.isaacbrodsky.freeze.elements.Lion;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;

/**
 * @author isaac
 * 
 */
public class Pairer extends Lion {
	@Override
	public int getDisplayCharacter() {
		return 229;
	}

	@Override
	public void tick(GameController game, Board board) {
		// sit there like a boss!
	}
}
