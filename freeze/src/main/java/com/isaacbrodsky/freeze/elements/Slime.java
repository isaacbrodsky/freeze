/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import java.util.Random;

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
public class Slime extends AbstractElement {
	private ElementColoring color;
	private int numInterval;

	public Slime() {
		this.color = new ElementColoring(0xFF,
				ElementColoring.ColorMode.RECESSIVE);
		numInterval = 4;

		this.numInterval = 2;// TODO ??
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getSaveData()
	 */
	@Override
	public SaveData getSaveData() {
		return new SaveData(0x25, color);
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
		return 1;// ?TODO hack
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		return 42;
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
		if (msg.isTouch()) {
			becomeBreakable(board);
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
		if (isBlockedIn(board)) {
			becomeBreakable(board);
			return;
		}

		// TODO check this
		if (getStats().p1 == numInterval) {
			spread(board);
			getStats().p1++;
		} else if (getStats().p1 > numInterval) {
			becomeBreakable(board);
		}
		Random r = new Random();
		if (r.nextInt(10) > 8)
			getStats().p1++;
	}

	private boolean isBlockedIn(Board board) {
		for (int i = 0; i < 4; i++) {
			int movX = OOPHelpers.getDirX(i);
			int movY = OOPHelpers.getDirY(i);

			if (board.boundsCheck(getX() + movX, getY() + movY) != -1)
				continue;

			Element at = board.elementAt(getX() + movX, getY() + movY);
			if (at == null
					|| at.getInteractionsRules().is(InteractionRule.FLOOR)) {
				return false;
			}
		}
		return true;
	}

	private void spread(Board board) {
		for (int i = 0; i < 4; i++) {
			int movX = OOPHelpers.getDirX(i);
			int movY = OOPHelpers.getDirY(i);

			if (board.boundsCheck(getX() + movX, getY() + movY) != -1)
				return;

			Element at = board.elementAt(getX() + movX, getY() + movY);
			if (at == null
					|| at.getInteractionsRules().is(InteractionRule.FLOOR)) {
				Element tempSlime = new Slime();
				tempSlime.createInstance(new SaveData(0x25, color.getCode()));
				tempSlime.setStats(new Stats(getX() + movX, getY() + movY, 0,
						numInterval, 0, 0, 0, getCycle(), 0, 0, -1, 0, ""));
				tempSlime.setXY(getX() + movX, getY() + movY);
				board.putElement(getX() + movX, getY() + movY, tempSlime);
			}
		}
	}

	private void becomeBreakable(Board board) {
		Element tempBreakable = new Breakable();
		board.removeAt(getX(), getY(), this);
		Element tempUnder = board.floorAt(getX(), getY());
		int code = 0;
		if (tempUnder instanceof Fake) {
			code = tempUnder.getColoring().getBackCode();
			board.removeAt(getX(), getY(), tempUnder);
		}
		tempBreakable.createInstance(new SaveData(17, (code << 4)
				| color.getCode()));
		tempBreakable.setXY(getX(), getY());
		board.putElement(getX(), getY(), tempBreakable);
	}
}
