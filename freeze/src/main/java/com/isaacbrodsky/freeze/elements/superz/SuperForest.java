/**
 * 
 */
package com.isaacbrodsky.freeze.elements.superz;

import com.isaacbrodsky.freeze.elements.Element;
import com.isaacbrodsky.freeze.elements.Forest;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;

/**
 * @author isaac
 * 
 */
public class SuperForest extends Forest {

	@Override
	public void message(GameController game, Board board, Message msg) {
		if (msg.isTouch()) {
			Element e = game.getElementResolver().resolve("floor", 0x0A,
					getX(), getY());
			board.putElement(getX(), getY(), e);
			board.putElement(getX(), getY(), e);
		}
	}
}
