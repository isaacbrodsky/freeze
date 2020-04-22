/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;

/**
 * @author isaac
 * 
 */
public class Invisible extends Solid {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Solid#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		return 32;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.elements.Solid#message(com.isaacbrodsky.freeze
	 * .game.GameController, com.isaacbrodsky.freeze.game.Board,
	 * com.isaacbrodsky.freeze.elements.oop.Message)
	 */
	@Override
	public void message(GameController game, Board board, Message msg) {
		if (msg.isTouch()) {
			board.removeAt(getX(), getY(), this);
			Element replacement = new Normal();
			replacement.createInstance(new SaveData(0x16, getColoring()
					.getCode()));
			replacement.setXY(getX(), getY());
			board.putElement(getX(), getY(), replacement);
			game.setMessage(":( Invisible walls.");
		}
	}
}
