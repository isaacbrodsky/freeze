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
public class Pusher extends AbstractElement {
	private ElementColoring color;

	public Pusher() {
		color = new ElementColoring(0xFF, ElementColoring.ColorMode.RECESSIVE);
	}

	@Override
	public void createInstance(SaveData dat) {
		this.color = new ElementColoring(dat.getColor(),
				ElementColoring.ColorMode.RECESSIVE);

	}

	public SaveData getSaveData() {
		return new SaveData(0x28, color);
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
		int stepX = 0, stepY = 1;
		if (getStats() != null) {
			stepX = getStats().getStepX();
			stepY = getStats().getStepY();
		}

		if (stepX == 1)
			return 16;
		else if (stepX == -1)
			return 17;
		else if (stepY == 1)
			return 31;
		else if (stepY == -1)
			return 30;
		return 2;
	}

	private static final InteractionRulesSet _IRS = new InteractionRulesSet(
			InteractionRule.NO_TRANSPORTER);

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
	 * @see com.isaacbrodsky.freeze.elements.Element#tick()
	 */
	@Override
	public void tick(GameController game, Board board) {
		OOPHelpers.tryMove(getX(), getY(), getStats().getStepX(), getStats()
				.getStepY(), board, this);
	}

}
