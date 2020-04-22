/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

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
public class Star extends AbstractElement {
	private int colorIndex, charIndex;
	/**
	 * Not the correct characters
	 */
	private static final char[] STAR_CHARS = new char[] { '/', 196, '\\', 179 };

	public Star() {
	}

	@Override
	public void createInstance(SaveData dat) {
		colorIndex = 0;
		charIndex = 0;
	}

	public SaveData getSaveData() {
		return new SaveData(0x0F, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getColoring()
	 */
	@Override
	public ElementColoring getColoring() {
		return Scroll.SCROLL_COLORS[colorIndex];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		return STAR_CHARS[charIndex];
	}

	private static final InteractionRulesSet _IRS = new InteractionRulesSet(
			InteractionRule.ITEM, InteractionRule.MOVEABLE_EW,
			InteractionRule.MOVEABLE_NS);

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
	 * com.isaacbrodsky.freeze.elements.Element#message(com.isaacbrodsky.freeze
	 * .game.GameController, com.isaacbrodsky.freeze.game.Board,
	 * com.isaacbrodsky.freeze.elements.oop.Message)
	 */
	@Override
	public void message(GameController game, Board board, Message msg) {
		if (msg.isTouch()) {
			game.getPlayer().message(game, board, Message.SHOT);
			die(board);
		} else if (msg.isBombed())
			die(board);
	}

	/**
	 * Removes this element
	 */
	private void die(Board board) {
		board.removeAt(getX(), getY(), this);
		if (board.elementAt(getX(), getY()) == null
				&& board.floorAt(getX(), getY()) == null)
			OOPHelpers.putEmpty(board, getX(), getY());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#tick()
	 */
	@Override
	public void tick(GameController game, Board board) {
		colorIndex++;
		charIndex++;

		if (colorIndex >= Scroll.SCROLL_COLORS.length)
			colorIndex = 0;
		if (charIndex >= STAR_CHARS.length)
			charIndex = 0;

		// Try to move toward player
		int moveDir = OOPHelpers.getDirFromStringArray(game, board, this,
				new String[] { "seek" });
		int movX = OOPHelpers.getDirX(moveDir);
		int movY = OOPHelpers.getDirY(moveDir);

		if (board.boundsCheck(getX() + movX, getY() + movY) != -1)
			return;

		Element at = board.elementAt(getX() + movX, getY() + movY);
		if (at instanceof Player
				|| (at != null && at.getInteractionsRules().is(
						InteractionRule.POINT_BLANK_SHOOTABLE))) {
			at.message(game, board, Message.SHOT);
			die(board);
			return;
		}

		OOPHelpers.tryMove(getX(), getY(), movX, movY, board, this, false);

		getStats().p2--;
		if (getStats().p2 == 0) {
			die(board);
			return;
		}
	}

	/**
	 * @return
	 */
	public boolean isPlayerShot() {
		return getStats().p1 == 0;
	}
}