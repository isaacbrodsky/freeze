/**
 * 
 */
package com.isaacbrodsky.freeze2.menus.stateditor;

import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.game.Stat;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.Renderer;
import com.isaacbrodsky.freeze2.menus.Menu;
import com.isaacbrodsky.freeze2.menus.Menu.SendMode;
import com.isaacbrodsky.freeze2.menus.MenuCallback;
import com.isaacbrodsky.freeze2.menus.MenuUtils;
import com.isaacbrodsky.freeze2.menus.UIInteraction;

import java.awt.event.KeyEvent;
import java.util.List;

/**
 * @author isaac
 * 
 */
public class ElementInput extends MenuUtils implements UIInteraction {
	private final GameController game;
	private final Board board;

	private String title;
	private int val;
	private UIInteraction select;

	/**
	 * @param curr -1 for no element selected
	 */
	public ElementInput(GameController game, Board board, String title, int curr) {
		this.game = game;
		this.board = board;
		select = null;
		this.title = title;
		this.val = curr;
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
			StringBuilder elementList = new StringBuilder();

			elementList.append("None");
			List<Stat> stats = board.getStats();
			for (int i = 1; i < stats.size(); i++) {
				elementList.append("\r\n").append(
						StatInput.makeStatTitle(game, board, stats.get(i), i));
			}

			select = new Menu(title + " " + Menu.SELECT_TEXT, elementList
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

	@Override
	public void render(Renderer renderer, int yoff, boolean focused) {
		String selectText = "";
		if (focused) {
			selectText = " \001RIGHT\002 to select";
		}
		renderInputMessage(renderer, yoff, title + selectText, focused);

		ElementColoring c = UI_TEXT_COLOR;
		if (focused)
			c = UI_SELECT_COLOR;
		String name;
		if (val <= 0)
			name = "(none)";
		else if (val < board.getStats().size())
			name = StatInput.makeStatTitle(game, board, board.getStats().get(val), val);
		else
			name = String.format("(invalid: %d)", val);

		renderer.renderText(0, yoff + 1, name, c);

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
