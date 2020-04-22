/**
 * 
 */
package com.isaacbrodsky.freeze.elements.superz;

import java.util.Random;

import com.isaacbrodsky.freeze.elements.AbstractElement;
import com.isaacbrodsky.freeze.elements.data.InteractionRule;
import com.isaacbrodsky.freeze.elements.data.InteractionRulesSet;
import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.graphics.ElementColoring;

/**
 * @author isaac
 * 
 */
public class Stone extends AbstractElement {
	private static final int Z_AMOUNT = 1;

	private ElementColoring color;
	private char c;

	public Stone() {
		randomize();
	}

	private void randomize() {
		Random r = new Random();
		c = (char) ('A' + r.nextInt(26));
		color = new ElementColoring(15 - r.nextInt(7),
				ElementColoring.ColorMode.DOMINANT);
	}

	private static final InteractionRulesSet _IRS = new InteractionRulesSet(
			InteractionRule.ITEM);

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
	 * @see com.isaacbrodsky.freeze.elements.Element#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		return c;
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
	 * @see
	 * com.isaacbrodsky.freeze.elements.Element#message(com.isaacbrodsky.freeze
	 * .game.GameController, com.isaacbrodsky.freeze.game.Board,
	 * com.isaacbrodsky.freeze.elements.oop.Message)
	 */
	@Override
	public void message(GameController game, Board board, Message msg) {
		if (msg.isTouch()) {
			if (game.getState().stones < 0)
				game.getState().stones = 0;
			game.getState().stones += Z_AMOUNT;
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
		randomize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.elements.Element#createInstance(com.isaacbrodsky
	 * .freeze.elements.data.SaveData)
	 */
	@Override
	public void createInstance(SaveData dat) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getSaveData()
	 */
	@Override
	public SaveData getSaveData() {
		return new SaveData(0x40, color.getCode());
	}
}