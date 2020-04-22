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
public class Duplicator extends AbstractElement {
	private SaveData sav;
	private ElementColoring color;
	private boolean alive;

	public Duplicator() {
		this.sav = null;
		this.color = new ElementColoring(0xFE,
				ElementColoring.ColorMode.CODOMINANT);
		alive = true;
	}

	public void createInstance(SaveData dat) {
		this.sav = dat;
		this.color = new ElementColoring(dat.getColor(),
				ElementColoring.ColorMode.CODOMINANT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getSaveData()
	 */
	@Override
	public SaveData getSaveData() {
		return sav;
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
		if (getStats() == null)
			return 0;
		return (9 - getStats().getP2()) * 3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		if (getStats() != null) {
			switch (getStats().getP1()) {
			case 2:
				return 249; // big dot
			case 3:
				return 248; // degree
			case 4:
				return 111; // o
			case 5:
				return 79; // O
			}

			return 250; // little dot
		}

		return 250;
		// old temp debug code
		// return '0' + getStats().getP1();
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
		int pos = getStats().p1 + 1;
		if (pos == 6) {
			if (alive)
				duplicate(game, board);
		}
		if (pos >= 6) {
			pos = 0;
		}
		getStats().p1 = pos;
	}

	private void duplicate(GameController game, Board board) {
		int stepX = getStats().getStepX();
		int stepY = getStats().getStepY();

		if (board.boundsCheck(getX() + stepX, getY() + stepY) == -1
				&& board.boundsCheck(getX() - stepX, getY() - stepY) == -1) {
			Element e = board.elementAt(getX() + stepX, getY() + stepY);
			Element blocking = board.elementAt(getX() - stepX, getY() - stepY);

			if (e == null)
				return;

			duplicateCheckBlocking(game, board, e, blocking);
			if (!alive)
				return;// don't proceed, will result in glitching the player
			// away from the duplicator.
			int ret = duplicateTryMove(board, stepX, stepY, blocking);

			if (ret == -1) {
				Element replacement = game.getElementResolver().resolve(
						e.getSaveData(), getX() - stepX, getY() - stepY);
				if (e.getStats() != null)
					replacement.setStats(e.getStats());

				board.putElement(getX() - stepX, getY() - stepY, replacement);
			}
		}
	}

	/**
	 * Determines if this duplicator is being blocked by the player. If so, it
	 * will set {@link #alive} to <code>false</code>.
	 * 
	 * <p>
	 * If the blocking element is the player and this duplicator is trying to
	 * duplicate certain types of objects this method wil throw an exception to
	 * maintain compatibility with the corresponding ZZT crash that would occur.
	 * 
	 * @param game
	 * @param board
	 * @param e
	 * @param blocking
	 * @throws RuntimeException
	 */
	private void duplicateCheckBlocking(GameController game, Board board,
			Element e, Element blocking) throws RuntimeException {
		Class<? extends Element> clazz = e.getClass();
		if (blocking instanceof Player) {
			e.message(game, board, Message.TOUCH);
			e.message(game, board, Message.SHOT);
			// TODO check duplicator messages
			alive = false;

			if (clazz.equals(Boulder.class)
					|| clazz.equals(Slider.SliderEW.class)
					|| clazz.equals(Slider.SliderNS.class)) {
				game.reportError("ZZT crahse when a boulder or slider"
						+ "\r\nis duplicated over a player.");
			}
		}
	}

	/**
	 * @param board
	 * @param stepX
	 * @param stepY
	 * @param blocking
	 * @return
	 */
	private int duplicateTryMove(Board board, int stepX, int stepY,
			Element blocking) {
		int ret = -1;
		if (blocking != null
				&& ((stepX != 0 && blocking.getInteractionsRules().is(
						InteractionRule.MOVEABLE_EW)) || (stepY != 0 && blocking
						.getInteractionsRules().is(InteractionRule.MOVEABLE_NS)))) {
			ret = OOPHelpers.tryMove(blocking.getX(), blocking.getY(), -stepX,
					-stepY, board, blocking, true);
		}
		return ret;
	}
}
