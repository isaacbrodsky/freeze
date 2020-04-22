/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import com.isaacbrodsky.freeze.elements.data.InteractionRule;
import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.elements.oop.OOPHelpers;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;

/**
 * @author isaac
 * 
 */
public class BlinkWall extends Solid {
	private boolean fired = false;

	@Override
	public int getDisplayCharacter() {
		return 206;
	}

	@Override
	public void tick(GameController game, Board board) {
		if (getStats().p3 == 0)
			getStats().p3 = getStats().getP1() + 1;
		if (getStats().p3 == 1) {
			fire(game, board);

			getStats().p3 = getFireInterval();
			fired = !fired;
		} else {
			getStats().p3--;
		}
	}

	private int getFireInterval() {
		return (getStats().getP2() * 2) + 1;
	}

	private void fire(GameController game, Board board) {
		Element e = null;
		boolean edge = false;
		int x = getX() + getStats().getStepX();
		int y = getY() + getStats().getStepY();
		e = board.elementAt(x, y);
		while (!edge) {
			if (e != null) {
				if (fired) {
					if (!isMatch(e))
						return;
				} else {
					if (e instanceof Player) {
						zapPlayer(game, board, e);
						return;
					} else if (e.getInteractionsRules().is(
							InteractionRule.POINT_BLANK_SHOOTABLE)) {
						board.removeAt(x, y, e);
						e = board.elementAt(x, y);
					} else {
						return;
					}
				}
			}

			if (fired) {
				board.removeAt(x, y);
				OOPHelpers.putEmpty(board, x, y);
			} else {
				BlinkWallRay bw = ((getStats().getStepX() != 0) ? new BlinkWallRay.HorizontalRay()
						: new BlinkWallRay.VerticalRay());
				bw.createInstance(new SaveData(
						((getStats().getStepX() != 0) ? game
								.getElementResolver().codeFromClass(
										BlinkWallRay.HorizontalRay.class)
								: game.getElementResolver().codeFromClass(
										BlinkWallRay.VerticalRay.class)),
						getColoring().getCode()));
				bw.setXY(x, y);
				board.putElement(x, y, bw);
			}
			x += getStats().getStepX();
			y += getStats().getStepY();
			if (board.boundsCheck(x, y) != -1) {
				edge = true;
			} else {
				e = board.elementAt(x, y);
			}
		}
	}

	private boolean isMatch(Element e) {
		if (getStats().getStepX() != 0)
			return e.getClass().equals(BlinkWallRay.HorizontalRay.class);
		else
			return e.getClass().equals(BlinkWallRay.VerticalRay.class);
	}

	/**
	 * @param game
	 * @param board
	 * @param e
	 */
	private void zapPlayer(GameController game, Board board, Element e) {
		int oldX = e.getX(), oldY = e.getY();
		((Player) e).takeDamage(game, board, 0);
		e.message(game, board, Message.SHOT);
		if (e.getX() != oldX || e.getY() != oldY)
			return;

		if (getStats().getStepY() != 0) {
			if (board.boundsCheck(e.getX() + 1, e.getY()) == -1) {
				OOPHelpers.moveElementAbs(e.getX(), e.getY(), e.getX() + 1, e
						.getY(), board, e);
			}
		} else { // horiz
			Element blocking = null;
			if (board.boundsCheck(e.getX(), e.getY() - 1) == -1) {
				blocking = board.elementAt(e.getX(), e.getY() - 1);
			}
			int yadj = -1;
			if (blocking instanceof BlinkWallRay) {
				yadj = 1;
			}
			if (board.boundsCheck(e.getX(), e.getY() + yadj) == -1) {
				OOPHelpers.moveElementAbs(e.getX(), e.getY(), e.getX(), e
						.getY()
						+ yadj, board, e);
			}
		}
	}
}
