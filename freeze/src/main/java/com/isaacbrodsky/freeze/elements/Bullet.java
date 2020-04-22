/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import com.isaacbrodsky.freeze.elements.data.*;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.graphics.ElementColoring;

/**
 * @author isaac
 * 
 */
public class Bullet extends AbstractElement {
	private ElementColoring color;

	public Bullet() {
		this.color = new ElementColoring(0x0F,
				ElementColoring.ColorMode.RECESSIVE);
	}

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
		return new SaveData(18, color);
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
		return 248;
	}

	private static final InteractionRulesSet _IRS = new InteractionRulesSet(
			InteractionRule.ITEM, InteractionRule.BLOCKS_MOVE,
			InteractionRule.POINT_BLANK_SHOOTABLE);

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
			die(board);
		} else if (msg.isTouch()) {
			game.getPlayer().message(game, board, Message.SHOT);
			die(board);
		}
	}

	/**
	 * @param game
	 * @param board
	 * @param xS
	 * @param yS
	 * @return
	 */
	private boolean onShot(GameController game, Board board, int xS, int yS) {
		if (xS == getStats().stepX && yS == getStats().stepY) {
			tick(game, board);
			skipTick = true;
			return true;
		}
		return false;
	}

	/**
	 * Removes this element
	 */
	private void die(Board board) {
		board.removeAt(getX(), getY(), this);
	}

	private boolean skipTick = false, ricocheted;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#tick()
	 */
	@Override
	public void tick(GameController game, Board board) {
		ricocheted = false;
		processTick(game, board);
	}

	/**
	 * @param game
	 * @param board
	 */
	private void processTick(GameController game, Board board) {
		int x = getX();
		int y = getY();

		Element thisE = board.elementAt(x, y);
		if (thisE == null || thisE != this)
			return; // this element has been deleted already

		if (skipTick) {
			skipTick = false;
			return;
		}

		die(board);
		if (board.boundsCheck(x + getStats().stepX, y + getStats().stepY) != -1) {
			return;
		}

		Element e = board.elementAt(x + getStats().stepX, y + getStats().stepY);
		if (e != null) {
			if (e instanceof Bullet) {
				Bullet b = (Bullet) e;
				if (b == this)
					return;// no recursing
				if (b.isPlayerShot() && !isPlayerShot())
					return;// give player shot right of way or w/e
				b.onShot(game, board, getStats().stepX, getStats().stepY);
				e = board.elementAt(x + getStats().stepX, y + getStats().stepY);
			}
			if (checkRicochet(game, board, e))
				return;
		}
		finalCollision(game, board, x, y, e);
	}

	/**
	 * @param game
	 * @param board
	 * @param e
	 */
	private boolean checkRicochet(GameController game, Board board, Element e) {
		if (e instanceof Ricochet) {
			if (ricocheted) {
				return true;
			}
			getStats().stepX = -getStats().stepX;
			getStats().stepY = -getStats().stepY;
			ricocheted = true;
			board.putElement(getX(), getY(), this);
			processTick(game, board);
			return true;
		}
		return false;
	}

	/**
	 * @param game
	 * @param board
	 * @param x
	 * @param y
	 * @param e
	 */
	private void finalCollision(GameController game, Board board, int x, int y,
			Element e) {
		if (e != null
				&& !(e.getInteractionsRules().is(InteractionRule.SHOOTOVER))) {
			// check for ricocheting
			if (checkSideRicochet(game, board, x, y, e))
				return;

			if (!e.getInteractionsRules().is(
					InteractionRule.ONLY_PLAYER_SHOOTABLE)
					|| isPlayerShot())
				e.message(game, board, Message.SHOT);
		} else {
			setXY(x + getStats().stepX, y + getStats().stepY);
			board.putElement(getX(), getY(), this);
		}
	}

	/**
	 * @param game
	 * @param board
	 * @param x
	 * @param y
	 * @param e
	 */
	private boolean checkSideRicochet(GameController game, Board board, int x,
			int y, Element e) {
		if (!e.getInteractionsRules().is(InteractionRule.POINT_BLANK_SHOOTABLE)) {
			Element right = null, left = null;

			if (getStats().stepX == 0) {
				if (board.boundsCheck(x + 1, y) == -1)
					right = board.elementAt(x + 1, y);
				if (board.boundsCheck(x - 1, y) == -1)
					left = board.elementAt(x - 1, y);
			} else if (getStats().stepY == 0) {
				if (board.boundsCheck(x, y + 1) == -1)
					right = board.elementAt(x, y + 1);
				if (board.boundsCheck(x, y - 1) == -1)
					left = board.elementAt(x, y - 1);
			}

			if (right instanceof Ricochet || left instanceof Ricochet) {
				if (getStats().stepX == 0) {
					if (ricocheted)
						return true;
					ricocheted = true;
					getStats().stepX = (right instanceof Ricochet) ? -1 : 1;
					getStats().stepY = 0;
					board.putElement(getX(), getY(), this);
					processTick(game, board);
					return true;
				}
				if (getStats().stepY == 0) {
					if (ricocheted)
						return true;
					ricocheted = true;
					getStats().stepX = 0;
					getStats().stepY = (right instanceof Ricochet) ? -1 : 1;
					board.putElement(getX(), getY(), this);
					processTick(game, board);
					return true;
				}
			}
		}
		return false;
	}

	public boolean isPlayerShot() {
		if (getStats() == null)
			return false;
		return getStats().p1 == 0;
	}

}
