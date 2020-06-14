/**
 * 
 */
package com.isaacbrodsky.freeze2.game.editor;

import com.isaacbrodsky.freeze2.elements.CommonElements;
import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.Stat;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.graphics.Renderer;
import com.isaacbrodsky.freeze2.menus.MenuUtils;
import com.isaacbrodsky.freeze2.menus.TypingInteraction;
import com.isaacbrodsky.freeze2.menus.UIInteraction;

import java.awt.event.KeyEvent;

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

	public TypingInput(Board board, int type, Cursor cur) {
		this.stillAlive = true;

		this.board = board;

		this.type = type;

		this.cur = cur;
	}

	@Override
	public boolean keyTyped(int key, int mod) {
		Stat player = board.getPlayer();
		int step = 1;
		if (key == KeyEvent.VK_BACK_SPACE) {
			key = ' ';
			step = 0;
			cur.moveX(-1, board.getWidth());
		}

		if (player.x != cur.getX() || player.y != cur.getY()) {
			// Do not permit overwriting the player element
			Tile t = new Tile(type, key);
			board.putTileAndStats(cur.getX(), cur.getY(), t);
		}

		cur.moveX(step, board.getWidth());
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
		renderer.renderText(cur.getX() - px - 1, cur.getY() - py - 1, 219,
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
