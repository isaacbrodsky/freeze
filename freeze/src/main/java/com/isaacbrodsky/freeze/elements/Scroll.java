/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import com.isaacbrodsky.freeze.elements.data.InteractionRule;
import com.isaacbrodsky.freeze.elements.data.InteractionRulesSet;
import com.isaacbrodsky.freeze.elements.data.Stats;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.graphics.ElementColoring;

/**
 * @author isaac
 * 
 */
public class Scroll extends ZObject {
	private int colorIndex = 0;
	public static final ElementColoring[] SCROLL_COLORS;
	private int recursion;

	static {
		SCROLL_COLORS = new ElementColoring[7];
		for (int i = 0; i < SCROLL_COLORS.length; i++)
			SCROLL_COLORS[i] = new ElementColoring(9 + i);
	}

	public Scroll() {
		this.colorIndex = 0;
		this.recursion = 0;
	}

	@Override
	public void setStats(Stats s) {
		super.setStats(s);
		getStats().currInstr = -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		return 232;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getColoring()
	 */
	@Override
	public ElementColoring getColoring() {
		return SCROLL_COLORS[colorIndex];
	}

	private static final InteractionRulesSet _IRS = new InteractionRulesSet(
			InteractionRule.ITEM, InteractionRule.MOVEABLE_EW,
			InteractionRule.MOVEABLE_NS, InteractionRule.BLOCKS_MOVE);

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
			getStats().currInstr = 0;
		} else {
			super.message(game, board, msg);// may not properly emulate message
			// handling
		}
		tick(game, board);
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
		colorIndex++;
		if (colorIndex == SCROLL_COLORS.length)
			colorIndex = 0;

		super.tick(game, board);

		if (getStats().currInstr >= 0)
			board.removeAt(getX(), getY(), this);// Let's hope this works right
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.elements.ZObject#processCmd(com.isaacbrodsky
	 * .freeze.game.GameController, com.isaacbrodsky.freeze.game.Board,
	 * java.lang.String)
	 */
	@Override
	protected boolean processCmd(GameController game, Board board, String cmd) {
		// check if cmd is legal or not
		if (cmd.equalsIgnoreCase("#die")) {
			game.reportError("Scrolls may not #DIE");
			return false;
		}
		recursion++;
		if (recursion > 100) {
			game.reportError("Scroll tried to execute too many commands.\n"
					+ "Aborted to prevent infinite loops.");
			return false;
		}

		return super.processCmd(game, board, cmd);
	}
}
