/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import com.isaacbrodsky.freeze.game.Board;

/**
 * @author isaac
 * 
 */
public class Line extends Solid {
	private int dir;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		switch (getDir()) {
		case 0:
			return 46;
		case 1:
			return 181;
		case 2:
			return 198;
		case 3:
			return 205;
		case 4:
			return 208;
		case 5:
			return 188;
		case 6:
			return 200;
		case 7:
			return 202;
		case 8:
			return 210;
		case 9:
			return 187;
		case 10:
			return 201;
		case 11:
			return 203;
		case 12:
			return 186;
		case 13:
			return 185;
		case 14:
			return 204;
		case 15:
		}
		return 206;
	}

	public int getDir() {
		return dir;
	}

	/**
	 * @param board
	 */
	public void recalculateLineWalls(Board board) {
		if (board == null) {
			dir |= 1 | 2 | 4 | 8;
			return;
		}
		
		int x = getX();
		int y = getY();

		dir = 0;
		if (board.boundsCheck(x + -1, y + 0) != -1
				|| (at(board, x + -1, y + 0) != null && at(board, x + -1, y + 0)
						.getClass().equals(getClass()))) {
			dir |= 1;
		}
		if (board.boundsCheck(x + 1, y + 0) != -1
				|| (at(board, x + 1, y + 0) != null && at(board, x + 1, y + 0)
						.getClass().equals(getClass()))) {
			dir |= 2;
		}
		if (board.boundsCheck(x + 0, y + -1) != -1
				|| (at(board, x + 0, y + -1) != null && at(board, x + 0, y + -1)
						.getClass().equals(getClass()))) {
			dir |= 4;
		}
		if (board.boundsCheck(x + 0, y + 1) != -1
				|| (at(board, x + 0, y + 1) != null && at(board, x + 0, y + 1)
						.getClass().equals(getClass()))) {
			dir |= 8;
		}
	}

	private Element at(Board b, int x, int y) {
		if (!this.getClass().equals(Line.class))
			return b.floorAt(x, y);
		else
			return b.elementAt(x, y);
	}
}
