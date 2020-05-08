/**
 * 
 */
package com.isaacbrodsky.freeze2.game.editor;

import com.isaacbrodsky.freeze2.game.ZBoard;
import com.isaacbrodsky.freeze2.graphics.Renderer;

/**
 * @author isaac
 * 
 */
public class Cursor {

	private int curX, curY;

	public Cursor() {
		curX = ZBoard.BOARD_WIDTH / 2;
		curY = ZBoard.BOARD_HEIGHT / 2;
	}

	public Cursor(int x, int y) {
		this.curX = x;
		this.curY = y;
	}

	public Cursor(Cursor other) {
		this.curX = other.getX();
		this.curY = other.getY();
	}

	/**
	 * @param xStep
	 * @param yStep
	 */
	public void moveXY(int xStep, int yStep, int xLim, int yLim) {
		moveX(xStep, xLim);
		moveY(yStep, yLim);
	}

	public void moveX(int xStep, int xLim) {
		curX += xStep;
		if (curX < 1)
			curX = 1;
		if (curX >= xLim + 1)
			curX = xLim;
	}

	public void moveY(int yStep, int yLim) {
		curY += yStep;
		if (curY < 1)
			curY = 1;
		if (curY >= yLim + 1)
			curY = yLim;
	}

	public int getX() {
		return curX;
	}

	public int getY() {
		return curY;
	}

	public void render(Renderer renderer, boolean blinking, int xoff, int yoff, int ch) {
		if (blinking)
			renderer.renderText(curX - xoff - 1, curY - yoff - 1, ch,
					Renderer.SYS_COLOR);
	}

	@Override
	public String toString() {
		return "(" + String.format("%02d", curX) + ","
				+ String.format("%02d", curY) + ")";
	}
}
