/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

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
public class Key extends AbstractElement {
	private ElementColoring color;
	private boolean pickupable;

	@Override
	public void createInstance(SaveData dat) {
		this.color = new ElementColoring(dat.getColor(),
				ElementColoring.ColorMode.RECESSIVE);
	}

	@Override
	public SaveData getSaveData() {
		return new SaveData(0x08, color);
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
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		return 12;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getInteractionsRules()
	 */
	@Override
	public InteractionRulesSet getInteractionsRules() {
		if (pickupable) {
			return new InteractionRulesSet(InteractionRule.ITEM,
					InteractionRule.MOVEABLE_EW, InteractionRule.MOVEABLE_NS,
					InteractionRule.NOT_PLAYER_MOVEABLE);
		} else {
			return new InteractionRulesSet(InteractionRule.MOVEABLE_EW,
					InteractionRule.MOVEABLE_NS,
					InteractionRule.NOT_PLAYER_MOVEABLE);
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
		tick(game, board);
		if (msg.isTouch()) {
			int code = color.getForeCode();
			if (code < 6)
				code += 8;
			if (!pickupable) {
				game.setMessage("You already have a "
						+ ElementColoring.nameFromCode(code) + " key!");
			} else {
				game.setMessage("You now have the "
						+ ElementColoring.nameFromCode(code) + " key.");
				toggleMe(game);
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
		pickupable = getPickupable(game);
	}

	/**
	 * Toggle the associated status counter
	 */
	private void toggleMe(GameController game) {
		if (color.getForeCode() == 0) {
			game.getState().gems = game.getState().gems | 256;
		} else {
			int code = color.getForeCode() - 1;
			if (code > 6)
				code -= 8;
			if (code < 0) {
				if (code == -1)
					code = 6;
			}
			game.getState().keys[code] = 1;
		}
	}

	private boolean getPickupable(GameController game) {
		if (color.getForeCode() == 0) {
			return ((game.getState().gems & 256) != 256);
		} else {
			int code = color.getForeCode() - 1;
			if (code > 6)
				code -= 8;
			if (code < 0) {
				if (code == -1)
					code = 6;
			}
			return (game.getState().keys[code] == 0);
		}
	}

}
