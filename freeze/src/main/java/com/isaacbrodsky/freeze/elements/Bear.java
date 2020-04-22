/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.elements.oop.OOPHelpers;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;

/**
 * @author isaac
 * 
 * p1 - sensitivity
 */
public class Bear extends Lion {

	public Bear() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		return 153;
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
		if (msg.isShot()) {
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
		Player p = game.getPlayer();
		if (p == null)
			return;

		boolean doMove;
		if (p.getX() == getX() || 8 - getStats().getP1() < Math.abs(p.getY() - getY())) {
			if (8 - getStats().getP1() < Math.abs(p.getX() - getX())) {
				doMove = false;
			} else {
				doMove = true;
			}
		} else {
			doMove = true;
		}

		if (doMove)
			handleMove(game, board, "seek");
	}

	/**
	 * @param game
	 * @param board
	 * @param moveDirStr
	 */
	@Override
	protected void handleMove(GameController game, Board board,
			String moveDirStr) {
		int moveDir = OOPHelpers.getDirFromStringArray(game, board, this,
				new String[] { moveDirStr });
		int movX = OOPHelpers.getDirX(moveDir);
		int movY = OOPHelpers.getDirY(moveDir);

		if (board.boundsCheck(getX() + movX, getY() + movY) != -1)
			return;

		Element at = board.elementAt(getX() + movX, getY() + movY);
		if (at instanceof Breakable) {
			board.removeAt(getX() + movX, getY() + movY, at);
			board.removeAt(getX(), getY(), this);
			return;
		}
		if (at instanceof Player) {
			message(game, board, Message.TOUCH);
			board.removeAt(getX(), getY(), this);
			return;
		}

		OOPHelpers.tryMove(getX(), getY(), movX, movY, board, this, false);
	}
}
