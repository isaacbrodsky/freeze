/**
 * 
 */
package com.isaacbrodsky.freeze2.menus.stateditor;

import com.isaacbrodsky.freeze2.elements.Element;
import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.Renderer;
import com.isaacbrodsky.freeze2.menus.*;
import com.isaacbrodsky.freeze2.menus.Menu.SendMode;

import java.awt.event.KeyEvent;

/**
 * @author isaac
 * 
 */
public class TileInput extends MenuUtils implements UIInteraction {
	private final GameController game;

	private String title;
	private int val;
	private UIInteraction select;

	public TileInput(String title, int curr, GameController game) {
		select = null;
		this.title = title;
		this.val = curr;
		this.game = game;
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
			StringBuilder tileList = new StringBuilder();

			boolean foundElementIdForVal = false;
			int elementIdForVal = 0;
			// Don't permit invalid elements
			for (Element e : game.getElements().allElements()) {
				if (tileList.length() > 0)
					tileList.append("\r\n");
				tileList.append("!" + e.code() + ";" + e.name());
				if (e.code() == val) {
					foundElementIdForVal = true;
				} else if (!foundElementIdForVal) {
					elementIdForVal++;
				}
			}
			tileList
					.append("\r\n")
					.append("!-1;Enter custom...");

			select = new Menu(title + " " + Menu.SELECT_TEXT, tileList
					.toString(), new MenuCallback() {
				@Override
				public void menuCommand(String cmd, Object rider) {
					if (cmd != null) {
						int newVal = Integer.parseInt(cmd);
						if (newVal == -1) {
							select = new TextInput("Enter type code", Integer.toString(val), new MenuCallback() {
								@Override
								public void menuCommand(String cmd, Object rider) {
									if (cmd != null) {
										val = Integer.parseInt(cmd);
									}
								}
							}, true, null);
						} else {
							val = Integer.parseInt(cmd);
						}
					}
				}
			}, SendMode.NO, true);
			for (int j = 0; j < elementIdForVal; j++) {
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
		String name = game.resolveElement(val).name();

		renderer.renderText(0, yoff + 1, val + ": " + name, c);

		if (select != null)
			select.render(renderer, 0, focused);
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
