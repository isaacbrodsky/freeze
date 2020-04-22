/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import com.isaacbrodsky.freeze.elements.data.InteractionRulesSet;
import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.elements.oop.OOPHelpers;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.graphics.ElementColoring;

/**
 * An unimplemented element is either a glitch element that should not exist
 * (e.g. a world file edited to include an element with an invalid element type
 * code.) or an element that this system does not properly implement yet.
 * 
 * @author isaac
 */
public class Unimplemented extends AbstractElement {
	private SaveData dat;
	private int charIndex;
	private ElementColoring color;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.elements.Element#createInstance(com.isaacbrodsky
	 * .freeze.elements.data.SaveData)
	 */
	@Override
	public void createInstance(SaveData dat) {
		this.dat = dat;
		this.color = new ElementColoring(dat.getColor(),
				ElementColoring.ColorMode.DOMINANT);
		charIndex = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getSaveData()
	 */
	@Override
	public SaveData getSaveData() {
		return dat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getInteractionsRules()
	 */
	@Override
	public InteractionRulesSet getInteractionsRules() {
		return new InteractionRulesSet();
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
		game.reportError("This element is unimplemented.\r\n\r\n$Details"
				+ "\r\nType ID: " + dat.getType() + "\r\nColor: " + color
				+ " (" + color.getCode() + ")"
				+ "\r\n\r\nThis element will now disappear.");
		board.removeAt(getX(), getY(), this);
		OOPHelpers.putEmpty(board, getX(), getY());// otherwise its null
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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		// UNSAFE HACK
		// .. but cool looking - shouldn't be doing logic in this function
		charIndex++;
		if (charIndex == 256)
			charIndex = 0;
		// UNSAFE HACK

		return charIndex;
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

}
