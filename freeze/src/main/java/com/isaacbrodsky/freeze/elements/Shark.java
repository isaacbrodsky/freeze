/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import java.util.Random;

import com.isaacbrodsky.freeze.elements.data.InteractionRulesSet;
import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.elements.oop.OOPHelpers;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.graphics.ElementColoring;

/**
 * @author isaac
 * 
 */
public class Shark extends Lion {
	private ElementColoring color;

	public Shark() {
		this.color = new ElementColoring(0x07,
				ElementColoring.ColorMode.RECESSIVE);
	}

	@Override
	public void createInstance(SaveData dat) {
		super.createInstance(dat);

		this.color = new ElementColoring(dat.getColor(),
				ElementColoring.ColorMode.RECESSIVE);
	}

	@Override
	public ElementColoring getColoring() {
		return color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		return 94;
	}

	private static final InteractionRulesSet _IRS = new InteractionRulesSet();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getInteractionsRules()
	 */
	@Override
	public InteractionRulesSet getInteractionsRules() {
		return _IRS;
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
		if (msg.isBombed()) {
			board.removeAt(getX(), getY());
			game.getState().score += 1;
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
		if (r.nextInt(10) > getStats().getP1()) {
			handleMove(game, board, "rnd");
		} else {
			handleMove(game, board, "seek");
		}
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
		if (at instanceof Player) {
			message(game, board, Message.SHOT);
			board.removeAt(getX(), getY(), this);
			return;
		} else if (at instanceof Water) {
			OOPHelpers.moveElement(getX(), getY(), movX, movY, board, this);
		}
	}
}
