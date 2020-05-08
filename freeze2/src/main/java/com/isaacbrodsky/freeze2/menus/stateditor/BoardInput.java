/**
 * 
 */
package com.isaacbrodsky.freeze2.menus.stateditor;

import java.awt.event.KeyEvent;

import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.Renderer;
import com.isaacbrodsky.freeze2.menus.Menu;
import com.isaacbrodsky.freeze2.menus.Menu.SendMode;
import com.isaacbrodsky.freeze2.menus.MenuCallback;
import com.isaacbrodsky.freeze2.menus.MenuUtils;
import com.isaacbrodsky.freeze2.menus.UIInteraction;

/**
 * @author isaac
 * 
 */
public class BoardInput extends MenuUtils implements UIInteraction {
	private final GameController game;
	private final boolean permitTitle;

	private String title;
	private int val;
	private UIInteraction select;

	public BoardInput(String title, int curr, GameController game, boolean permitTitle) {
		select = null;
		this.title = title;
		this.val = curr;
		this.game = game;
		this.permitTitle = permitTitle;
	}

	@Override
	public String getSelectedLabel() {
		return Integer.toString(val);
	}

	@Override
	public String getSelectedText() {
		return Integer.toString(val);
	}

	@Override
	public boolean keyPress(int key) {
		if (select != null)
			return select.keyPress(key);
		else if (key == KeyEvent.VK_RIGHT) {
			StringBuilder boardList = new StringBuilder();

			int i = 0;
			if (!permitTitle) {
				boardList.append("None");
				i++;
			}
			for (; i < game.getBoardList().size(); i++) {
				if (boardList.length() > 0)
					boardList.append("\r\n");
				boardList.append(game.getBoardList().get(i).getState().boardName);
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
			for (int j = 0; j < val; j++) {
				select.keyPress(KeyEvent.VK_DOWN);
			}

			return true;
		}
		return false;
	}

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

	@Override
	public boolean stillAlive() {
		return true;
	}

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
