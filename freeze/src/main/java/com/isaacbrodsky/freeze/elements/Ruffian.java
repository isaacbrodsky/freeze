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
public class Ruffian extends Lion {
	private int currTime, currDir, numMove, currMove;

	public Ruffian() {
		currTime = currDir = 0;
		numMove = currMove = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		return 5;
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
		if (numMove >= currMove) {
			handleMove(game, board, currDir);
			currMove++;
		}

		// p1 = intel
		// p2 = rest time

		numMove = currMove = 0;
		currTime++;
		if (currTime >= getStats().getP2() + 1) {
			currTime = 0;

			// change direction now

			Random r = new Random();
			if (r.nextInt(10) > getStats().getP1()) {
				currDir = OOPHelpers.getDirFromStringArray(game, board, this,
						new String[] { "rnd" });
			} else {
				currDir = OOPHelpers.getDirFromStringArray(game, board, this,
						new String[] { "seek" });
			}
			numMove = r.nextInt(getStats().getP2() + 1) / 2;
			numMove = numMove == 0 ? 1 : numMove;

			handleMove(game, board, currDir);
		}
	}
}
