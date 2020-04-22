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
 * Also acts as a generic enemy (whose display character and tick() mechanics
 * are overriden)
 * 
 * @author isaac
 */
public class Lion extends AbstractElement {
	private ElementColoring color;
	private SaveData dat;

	public Lion() {
		this.color = new ElementColoring(0x0C,
				ElementColoring.ColorMode.DOMINANT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#createInstance(int, int)
	 */
	@Override
	public void createInstance(SaveData dat) {
		this.color = new ElementColoring(dat.getColor(),
				ElementColoring.ColorMode.DOMINANT);
		this.dat = dat;
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
		return 234;
	}

	private static final InteractionRulesSet _IRS = new InteractionRulesSet(
			InteractionRule.ITEM, InteractionRule.MOVEABLE_EW,
			InteractionRule.MOVEABLE_NS, InteractionRule.POINT_BLANK_SHOOTABLE,
			InteractionRule.ONLY_PLAYER_SHOOTABLE);

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
		if (msg.isShot()) {
			board.removeAt(getX(), getY(), this);
			game.getState().score += 1;
		} else if (msg.isTouch()) {
			game.getPlayer().message(game, board, Message.SHOT);
		} else if (msg.isBombed()) {
			board.removeAt(getX(), getY(), this);
			game.getState().score += 1;
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
		Random r = new Random();
		if (r.nextInt(10) > getStats().getP1()) {
			handleMove(game, board, "rnd");
		} else {
			handleMove(game, board, "seek");
		}
	}

	/**
	 * @param game
	 * @param board
	 * @param moveDirStr
	 */
	protected void handleMove(GameController game, Board board,
			String moveDirStr) {
		int moveDir = OOPHelpers.getDirFromStringArray(game, board, this,
				new String[] { moveDirStr });
		handleMove(game, board, moveDir);
	}

	/**
	 * @param game
	 * @param board
	 * @param moveDir
	 */
	protected void handleMove(GameController game, Board board, int moveDir) {
		int movX = OOPHelpers.getDirX(moveDir);
		int movY = OOPHelpers.getDirY(moveDir);

		if (board.boundsCheck(getX() + movX, getY() + movY) != -1)
			return;

		Element at = board.elementAt(getX() + movX, getY() + movY);
		if (at instanceof Player) {
			message(game, board, Message.TOUCH);
			board.removeAt(getX(), getY(), this);
			return;
		}

		getStats().stepX = movX;
		getStats().stepY = movY;

		OOPHelpers.tryMove(getX(), getY(), movX, movY, board, this, false);
	}
}
