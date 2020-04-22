/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import com.isaacbrodsky.freeze.elements.data.InteractionRule;
import com.isaacbrodsky.freeze.elements.data.InteractionRulesSet;
import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.data.Stats;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.elements.oop.OOPHelpers;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.graphics.ElementColoring;

/**
 * @author isaac
 * 
 */
public class Transporter extends AbstractElement {
	private static final char[][] TRANSPORTER_CHARS = new char[][] {
			new char[] { '-', '^', '~' }, new char[] { 179, ')', '>' },
			new char[] { '-', 'v', '_', 'v' }, new char[] { 179, '(', '<' },
			new char[] { '*', ' ' } // error case
	};

	private int charIndex;
	private ElementColoring color;
	private int dir, oppDir;

	public Transporter() {
		this.color = new ElementColoring(1, ElementColoring.ColorMode.RECESSIVE);
		dir = -1;
		charIndex = 0;
	}

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

	@Override
	public SaveData getSaveData() {
		return new SaveData(0x1E, color);
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
		if (dir < 0)
			return TRANSPORTER_CHARS[4][charIndex];
		else
			return TRANSPORTER_CHARS[dir][charIndex];
	}

	public int getDirection() {
		return dir;
	}

	public int getOppositeDirection() {
		return oppDir;
	}

	private static final InteractionRulesSet _IRS = new InteractionRulesSet(
			InteractionRule.BLOCKS_MOVE);

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
	 * @see com.isaacbrodsky.freeze.elements.Element#loadStats(int, int, int,
	 * int, int, int, int, int, int, java.lang.String)
	 */
	@Override
	public void setStats(Stats s) {
		super.setStats(s);
		if (s != null) {
			//cache information
			this.dir = OOPHelpers.getDir(s.getStepX(), s.getStepY());
			oppDir = OOPHelpers.getDir(-s.getStepX(), -s.getStepY());
		}
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
		if (msg.isTouch()) {
			Player p = board.getPlayer();
			if (p == null)
				return;

			teleportElement(board, p);
		}
	}

	private boolean teleportElementShort(Board board, Element target, int movX,
			int movY) {
		int nextX = OOPHelpers.getDirX(dir);
		int nextY = OOPHelpers.getDirY(dir);

		boolean doMove = false;
		if (board.boundsCheck(movX + nextX, movY + nextY) != -1) {
			return false;
		}
		Element blocker = board.elementAt(movX + nextX, movY + nextY);
		if (blocker == null
				|| blocker.getInteractionsRules().is(InteractionRule.FLOOR)) {
			doMove = true;
		} else if (nextX != 0) {
			if (blocker.getInteractionsRules().is(InteractionRule.MOVEABLE_EW)) {
				doMove = true;
			}
		} else if (nextY != 0) {
			if (blocker.getInteractionsRules().is(InteractionRule.MOVEABLE_NS)) {
				doMove = true;
			}
		}

		if (doMove) {
			OOPHelpers.moveElementAbs(target.getX(), target.getY(), movX, movY,
					board, target);
			int result = OOPHelpers.tryMove(target.getX(), target.getY(),
					nextX, nextY, board, target, true);
			if (result != -1) {
				OOPHelpers.moveElementAbs(target.getX(), target.getY(), movX,
						movY, board, target);
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * @param board
	 * @param target
	 */
	private void teleportElementLong(Board board, Element target) {
		int nextX = OOPHelpers.getDirX(dir);
		int nextY = OOPHelpers.getDirY(dir);
		boolean keepGoing = true;

		while (keepGoing) {
			if (board.boundsCheck(getX() + nextX, getY() + nextY) != -1) {
				return;
			}

			Element blocker = board.elementAt(getX() + nextX, getY() + nextY);
			if (blocker instanceof Transporter) {
				Transporter t = (Transporter) blocker;
				if (t.getOppositeDirection() == dir) {
					if (teleportElementShort(board, target, t.getX(), t.getY()))
						return;
				}
			}

			nextX += OOPHelpers.getDirX(dir);
			nextY += OOPHelpers.getDirY(dir);
		}
	}

	/**
	 * @param board
	 * @param target
	 */
	public void teleportElement(Board board, Element target) {
		if (target.getX() == getX() + OOPHelpers.getDirX(oppDir)
				&& target.getY() == getY() + OOPHelpers.getDirY(oppDir)) {

			int nextX = OOPHelpers.getDirX(dir);
			int nextY = OOPHelpers.getDirY(dir);
			Element blocker = board.elementAt(getX() + nextX, getY() + nextY);
			if (blocker != null) {
				if (!teleportElementShort(board, target, getX(), getY()))
					teleportElementLong(board, target);
			} else {
				OOPHelpers.moveElementAbs(target.getX(), target.getY(), getX()
						+ nextX, getY() + nextY, board, target);
			}
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
		charIndex++;
		int stack = 4;
		if (!(dir < 0))
			stack = dir;

		if (charIndex >= TRANSPORTER_CHARS[stack].length)
			charIndex = 0;
	}

}
