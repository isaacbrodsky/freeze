/**
 * 
 */
package com.isaacbrodsky.freeze.menus;

import java.awt.event.KeyEvent;

import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.graphics.ElementColoring;
import com.isaacbrodsky.freeze.graphics.Renderer;
import com.isaacbrodsky.freeze.menus.Menu.SendMode;

/**
 * @author isaac
 * 
 */
public class BoardInput extends MenuUtils implements UIInteraction {
	private String title;
	private int val;
	private GameController game;
	private UIInteraction select;

	/**
	 * @param title
	 * @param curr
	 * @param zEditorController
	 * @param b
	 */
	public BoardInput(String title, int curr, GameController game) {
		select = null;
		this.title = title;
		this.val = curr;
		this.game = game;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.menus.UIInteraction#getSelectedLabel()
	 */
	@Override
	public String getSelectedLabel() {
		return Integer.toString(val);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.menus.UIInteraction#getSelectedText()
	 */
	@Override
	public String getSelectedText() {
		return Integer.toString(val);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.menus.UIInteraction#keyPress(int)
	 */
	@Override
	public boolean keyPress(int key) {
		if (select != null)
			return select.keyPress(key);
		else if (key == KeyEvent.VK_RIGHT) {
			StringBuilder boardList = new StringBuilder();

			boardList.append("None");
			for (int i = 1; i < game.getBoardList().size(); i++) {
				boardList.append("\r\n").append(
						game.getBoardList().get(i).getState().boardName);
			}

			select = new Menu(title + " " + Menu.SELECT_TEXT, boardList
					.toString(), new MenuCallback() {
				@Override
				public void menuCommand(String cmd, Object rider) {
					if (cmd != null) {
						val = Integer.parseInt(cmd);
					}
				}
			}, SendMode.LASTNO, true);
			for (int i = 0; i < val; i++) {
				select.keyPress(KeyEvent.VK_DOWN);
			}

			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.menus.UIInteraction#render(com.isaacbrodsky
	 * .freeze.graphics.Renderer, int, boolean)
	 */
	@Override
	public void render(Renderer renderer, int yoff, boolean focused) {
		String selectText = "";
		if (focused) {
			selectText = " \001RIGHT\002 to select";
		}
		renderInputMessage(renderer, yoff, title + selectText, focused);

		Board target = game.getBoardList().get(val);
		ElementColoring c = UI_TEXT_COLOR;
		if (focused)
			c = UI_SELECT_COLOR;
		String name;
		if (target == null)
			name = "null";
		else if (val == 0)
			name = "None";
		else
			name = target.getState().boardName;

		renderer.renderText(0, yoff + 1, val + ": " + name, c);

		if (select != null)
			select.render(renderer, yoff, focused);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.menus.UIInteraction#stillAlive()
	 */
	@Override
	public boolean stillAlive() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.menus.UIInteraction#tick()
	 */
	@Override
	public void tick() {
		if (select != null) {
			select.tick();
			if (!select.stillAlive())
				select = null;
		}
	}

	@Override
	public UIInteraction getFocusedInteraction() {
		if (select != null)
			return select.getFocusedInteraction();
		return this;
	}
}
