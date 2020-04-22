/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import java.util.Random;

import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.elements.oop.OOPHelpers;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;

/**
 * @author isaac
 * 
 */
public class Tiger extends Lion {
	public Tiger() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		return 227;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.elements.Lion#message(com.isaacbrodsky.freeze
	 * .game.GameController, com.isaacbrodsky.freeze.game.Board,
	 * com.isaacbrodsky.freeze.elements.oop.Message)
	 */
	@Override
	public void message(GameController game, Board board, Message msg) {
		if (msg.isShot() || msg.isBombed()) {
			board.removeAt(getX(), getY());
			game.getState().score += 2;
		} else {
			super.message(game, board, msg);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.elements.Element#tick(com.isaacbrodsky.freeze
	 * .game.GameController, com.isaacbrodsky.freeze.game.Board)
	 */
	@Override
	public void tick(GameController game, Board board) {
		Random r = new Random();
		
		int rate = getStats().getP2();
		boolean typeShootsStars = false;
		if ((rate & 128) == 128) {
			rate = rate ^ 128;
			typeShootsStars = true;
		}
				
		if (r.nextInt(12) > rate) {
			Player p = board.getPlayer();
			if (p != null) {
				if (Math.abs(getX() - p.getX()) <= 2
						|| Math.abs(getY() - p.getY()) <= 2) {
					int actDir = OOPHelpers.getDirFromStringArray(game, board,
							this, new String[] { "seek" });
					int actX = OOPHelpers.getDirX(actDir);
					int actY = OOPHelpers.getDirY(actDir);

					if (typeShootsStars) {
						OOPHelpers.shoot(game, board, getX(), getY(), actX,
								actY, false, Star.class);
					} else {
						OOPHelpers.shoot(game, board, getX(), getY(), actX,
								actY, false, Bullet.class);
					}
				}
			}
		}

		if (r.nextInt(10) > getStats().getP1()) {
			super.handleMove(game, board, "rnd");
		} else {
			super.handleMove(game, board, "seek");
		}
	}
}
