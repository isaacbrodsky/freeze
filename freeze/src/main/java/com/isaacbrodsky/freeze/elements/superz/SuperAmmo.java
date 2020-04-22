/**
 * 
 */
package com.isaacbrodsky.freeze.elements.superz;

import com.isaacbrodsky.freeze.elements.Ammo;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;

/**
 * @author isaac
 * 
 */
public class SuperAmmo extends Ammo {
	private static final int AMMO_AMOUNT = 10;

	@Override
	public void message(GameController game, Board board, Message msg) {
		if (msg.isTouch()) {
			game.getState().ammo += AMMO_AMOUNT;
		}
	}

}
