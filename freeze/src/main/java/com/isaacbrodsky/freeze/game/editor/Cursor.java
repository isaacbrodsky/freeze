/**
 * 
 */
package com.isaacbrodsky.freeze.game.editor;

import com.isaacbrodsky.freeze.game.ZBoard;
import com.isaacbrodsky.freeze.graphics.Renderer;

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
		if (curX < 0)
			curX = 0;
		if (curX >= xLim)
			curX = xLim - 1;
	}

	public void moveY(int yStep, int yLim) {
		curY += yStep;
		if (curY < 0)
			curY = 0;
		if (curY >= yLim)
			curY = yLim - 1;
	}

	public int getX() {
		return curX;
	}

	public int getY() {
		return curY;
	}

	/**
	 * @param renderer
	 * @param blinking
	 */
	public void render(Renderer renderer, boolean blinking) {
		if (blinking)
			renderer.renderText(curX, curY, 197, Renderer.SYS_COLOR);
	}

	public void render(Renderer renderer, boolean blinking, int xoff, int yoff) {
		if (blinking)
			renderer.renderText(curX - xoff, curY - yoff, 197,
					Renderer.SYS_COLOR);
	}

	@Override
	public String toString() {
		return "(" + String.format("%02d", curX) + ","
				+ String.format("%02d", curY) + ")";
	}
}
