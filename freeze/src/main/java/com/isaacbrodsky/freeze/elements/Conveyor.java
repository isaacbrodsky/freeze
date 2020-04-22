/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import java.util.HashSet;
import java.util.Set;

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
public class Conveyor extends AbstractElement {
	private static final char[] CONVEYOR_CHARS = new char[] { 179, '/', 196,
			'\\', 179, '/', 196, '\\' };

	private static final int[][] CONVEYOR_DIRS = new int[][] { { -1, -1 },
			{ 0, -1 }, { -1, 0 }, { 1, 1 }, { 1, 0 }, { 0, 1 }, { -1, 1 },
			{ 1, -1 } };
	private static final int[][] CW_DIRS = new int[][] { { 1, 0 }, { 1, 0 },
			{ 0, -1 }, { -1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 }, { 0, 1 } };
	private static final int[][] CCW_DIRS = new int[][] { { 0, 1 }, { -1, 0 },
			{ 0, 1 }, { 0, -1 }, { 0, -1 }, { 1, 0 }, { 1, 0 }, { -1, 0 } };

	private static final int MAX_CONVEYS = 3;

	private ElementColoring color;
	private boolean cw;
	private int charIndex;

	private Conveyor() {
		charIndex = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#createInstance(int, int)
	 */
	@Override
	public void createInstance(SaveData dat) {
		if (dat.getType() == 16)
			cw = true;
		else
			cw = false;

		this.color = new ElementColoring(dat.getColor(),
				ElementColoring.ColorMode.RECESSIVE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getSaveData()
	 */
	public SaveData getSaveData() {
		if (cw)
			return new SaveData(16, color);
		else
			return new SaveData(17, color);
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
	 * @see com.isaacbrodsky.freeze.elements.Element#getCycle()
	 */
	@Override
	public int getCycle() {
		if (getStats() != null)
			return getStats().getCycle();
		else
			return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		return CONVEYOR_CHARS[charIndex];
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
	 * com.isaacbrodsky.freeze.elements.Element#message(com.isaacbrodsky.freeze
	 * .game.GameController, com.isaacbrodsky.freeze.game.Board,
	 * com.isaacbrodsky.freeze.elements.oop.Message)
	 */
	@Override
	public void message(GameController game, Board board, Message msg) {
		// nothing
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
		tickCharacter();

		Set<Element> thisConveyed = new HashSet<Element>();
		for (int coordsIdx = 0; coordsIdx < CONVEYOR_DIRS.length; coordsIdx++) {
			int[] coords = CONVEYOR_DIRS[coordsIdx];
			if (board.boundsCheck(getX() + coords[0], getY() + coords[1]) != -1)
				continue;

			Element e = board.elementAt(getX() + coords[0], getY() + coords[1]);
			if (e == null || e.getInteractionsRules().is(InteractionRule.FLOOR))
				continue;

			if (!(e.getInteractionsRules().is(InteractionRule.MOVEABLE_EW) && e
					.getInteractionsRules().is(InteractionRule.MOVEABLE_NS)))
				continue;

			Integer numTimesConveyed = board.getConveyedElements().get(e);
			if (numTimesConveyed != null
					&& numTimesConveyed.equals(MAX_CONVEYS))
				continue;
			if (thisConveyed.contains(e))
				continue;

			int convX, convY;
			if (cw) {
				convX = CW_DIRS[coordsIdx][0];
				convY = CW_DIRS[coordsIdx][1];
			} else {
				convX = CCW_DIRS[coordsIdx][0];
				convY = CCW_DIRS[coordsIdx][1];
			}

			if (board.boundsCheck(e.getX() + convX, e.getY() + convY) != -1)
				continue;

			Element blocking = board.elementAt(e.getX() + convX, e.getY()
					+ convY);
			if (blocking == null
					|| blocking.getInteractionsRules()
							.is(InteractionRule.FLOOR)) {
				OOPHelpers.moveElement(e.getX(), e.getY(), convX, convY, board,
						e);

				if (numTimesConveyed == null)
					numTimesConveyed = 0;
				numTimesConveyed = numTimesConveyed + 1;
				board.getConveyedElements().put(e, numTimesConveyed);
				thisConveyed.add(e);

				if (e instanceof Player) {
					// this seems necessary in order to prevent the player
					// from just running throught the conveyors in the Armory
					// board in TOWN.ZZT
					((Player) e).setXYStep(0, 0);
				}
			}
		}
	}

	/**
	 * 
	 */
	private void tickCharacter() {
		if (cw) {
			charIndex++;
			if (charIndex >= CONVEYOR_CHARS.length)
				charIndex = 0;
		} else {
			charIndex--;
			if (charIndex < 0)
				charIndex = CONVEYOR_CHARS.length - 1;
		}
	}

	public static class ConveyorCW extends Conveyor {
		public ConveyorCW() {
			super();
		}
	}

	public static class ConveyorCCW extends Conveyor {
		public ConveyorCCW() {
			super();
		}
	}
}
