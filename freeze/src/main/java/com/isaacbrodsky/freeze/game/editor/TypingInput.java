/**
 * 
 */
package com.isaacbrodsky.freeze.game.editor;

import java.awt.event.KeyEvent;

import com.isaacbrodsky.freeze.elements.Text;
import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.graphics.Renderer;
import com.isaacbrodsky.freeze.menus.*;
import com.isaacbrodsky.freeze.game.*;

/**
 * @author isaac
 * 
 */
public class TypingInput extends MenuUtils implements UIInteraction,
		TypingInteraction {
	private boolean stillAlive;

	private Board board;
	private int type;

	private Cursor cur;

	/**
	 * @param title
	 * @param data
	 * @param callback
	 * @param singleLetter
	 * @param cancellable
	 * @param rider
	 */
	public TypingInput(Board board, int type, Cursor cur) {
		this.stillAlive = true;

		this.board = board;

		this.type = type;

		this.cur = cur;
	}

	@Override
	public boolean keyTyped(int key, int mod) {
		Text t = new Text();
		t.createInstance(new SaveData(type, key));
		t.setXY(cur.getX(), cur.getY());
		board.removeAt(cur.getX(), cur.getY());
		board.removeAt(cur.getX(), cur.getY());
		board.putElement(cur.getX(), cur.getY(), t);

		// x++;
		// if (x >= board.getWidth())
		// x = board.getWidth() - 1;
		cur.moveX(1, board.getWidth());
		return true;
	}

	@Override
	public boolean keyPress(int key) {
		if (key == KeyEvent.VK_ESCAPE) {
			stillAlive = false;
		}
		return true;
	}

	@Override
	public String getSelectedText() {
		return null;
	}

	@Override
	public String getSelectedLabel() {
		return null;
	}

	@Override
	public void render(Renderer renderer, int yoff, boolean focused) {
		if (focused)
			render(renderer, 0, -yoff);
	}

	/**
	 * @param renderer
	 * @param px
	 * @param py
	 */
	public void render(Renderer renderer, int px, int py) {
		renderer.renderText(cur.getX() - px, cur.getY() - py, 219,
				MENU_ALT_COLOR);
	}

	@Override
	public void tick() {

	}

	@Override
	public boolean stillAlive() {
		return stillAlive;
	}

	@Override
	public UIInteraction getFocusedInteraction() {
		return this;
	}
}
