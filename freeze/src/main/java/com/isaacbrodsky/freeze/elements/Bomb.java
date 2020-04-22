/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import java.util.Random;

import com.isaacbrodsky.freeze.elements.data.InteractionRule;
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
public class Bomb extends AbstractElement {
	private static final int CIRC_W = 7;
	private static final int[] CIRC_ADJ = { 0, -1, -1, -2, -3 };
	private static final int CIRC_H = CIRC_ADJ.length - 1;// 4

	private ElementColoring color;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#createInstance(int, int)
	 */
	@Override
	public void createInstance(SaveData dat) {
		this.color = new ElementColoring(dat.getColor(),
				ElementColoring.ColorMode.RECESSIVE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getSaveData()
	 */
	@Override
	public SaveData getSaveData() {
		return new SaveData(0x0D, color.getCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getColoring()
	 */
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
		if (getStats() == null)
			return 11;
		if (getStats().p1 == 0 || getStats().p1 == 1)
			return 11;
		else
			return '0' + getStats().p1;
	}

	// private static final InteractionRulesSet _irs = new
	// InteractionRulesSet();
	private static final InteractionRulesSet _IRS = new InteractionRulesSet(
			InteractionRule.MOVEABLE_EW, InteractionRule.MOVEABLE_NS);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getInteractionsRules()
	 */
	@Override
	public InteractionRulesSet getInteractionsRules() {
		// if (stage == 0)
		// return _irs;
		// else
		return _IRS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.elements.Element#message(com.isaacbrodsky.freeze
	 * .game.GameController, com.isaacbrodsky.freeze.game.Board,
	 * com.isaacbrodsky.freeze.elements.oop.Message)
	 */
	@Override
	public void message(GameController game, Board board, Message msg) {
		if (msg.isTouch() && (getStats() == null || getStats().p1 == 0)) {
			game.setMessage("FIRE IN THE HOLE");
			if (getStats() != null)
				getStats().p1 = 9;
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
		if (getStats().p1 != 0) {
			getStats().p1--;

			if (getStats().p1 == 1) {
				// Set breakables & send bombed
				explode(game, board);
			} else if (getStats().p1 == 0) {
				// clear breakables and die
				clearExplosion(board);
			}
		}
	}

	/**
	 * @param board
	 */
	private void clearExplosion(Board board) {
		int insX, insY;
		for (int y = -CIRC_H; y <= CIRC_H; y++) {
			for (int x = -CIRC_W - CIRC_ADJ[Math.abs(y)]; x <= CIRC_W
					+ CIRC_ADJ[Math.abs(y)]; x++) {
				insX = getX() + x;
				insY = getY() + y;
				if (board.boundsCheck(insX, insY) == -1) {
					if (board.elementAt(insX, insY) instanceof Breakable) {
						board.removeAt(insX, insY);
						OOPHelpers.putEmpty(board, insX, insY);
					}
				}
			}
		}
		board.removeAt(getX(), getY(), this);
	}

	/**
	 * @param game
	 * @param board
	 */
	private void explode(GameController game, Board board) {
		Random r = new Random();
		int insX, insY;
		for (int y = -CIRC_H; y <= CIRC_H; y++) {
			for (int x = -CIRC_W - CIRC_ADJ[Math.abs(y)]; x <= CIRC_W
					+ CIRC_ADJ[Math.abs(y)]; x++) {
				insX = getX() + x;
				insY = getY() + y;
				if (board.boundsCheck(insX, insY) == -1) {
					if (board.elementAt(insX, insY) != null) {
						board.elementAt(insX, insY).message(game, board,
								Message.BOMBED);
					}
					if (board.elementAt(insX, insY) == null) {
						Element brkable = new Breakable();
						brkable.createInstance(new SaveData(0, 0x0F - r
								.nextInt(7)));
						brkable.setXY(insX, insY);

						board.putElement(insX, insY, brkable);
					}
				}
			}
		}
	}
}
