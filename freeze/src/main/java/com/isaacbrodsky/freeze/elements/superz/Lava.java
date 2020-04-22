/**
 * 
 */
package com.isaacbrodsky.freeze.elements.superz;

import com.isaacbrodsky.freeze.elements.Water;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;

public class Lava extends Water {
	@Override
	public int getDisplayCharacter() {
		return 'o';
	}
	
	@Override
	public void message(GameController game, Board board, Message msg) {
		if (msg.isTouch())
			game.setMessage("Your way is blocked by lava.");
	}
}