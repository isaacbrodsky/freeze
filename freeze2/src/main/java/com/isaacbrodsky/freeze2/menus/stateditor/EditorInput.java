/**
 * 
 */
package com.isaacbrodsky.freeze2.menus.stateditor;

import java.awt.event.KeyEvent;

import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.Renderer;
import com.isaacbrodsky.freeze2.menus.MenuCallback;
import com.isaacbrodsky.freeze2.menus.MenuUtils;
import com.isaacbrodsky.freeze2.menus.TypingInteraction;
import com.isaacbrodsky.freeze2.menus.UIInteraction;
import com.isaacbrodsky.freeze2.utils.StringUtils;

/**
 * @author isaac
 * 
 */
public class EditorInput extends MenuUtils implements TypingInteraction {
	private String title;
	private String val;
	private TypingInteraction select;

	public EditorInput(String title, String curr) {
		select = null;
		this.title = title;
		this.val = curr;
	}

	@Override
	public String getSelectedLabel() {
		return val;
	}

	@Override
	public String getSelectedText() {
		return val;
	}

	@Override
	public boolean keyTyped(int key, int mod) {
		if (select != null)
			return select.keyTyped(key, mod);
		return false;
	}

	@Override
	public boolean keyPress(int key) {
		if (select != null)
			return select.keyPress(key);
		else if (key == KeyEvent.VK_RIGHT) {
			select = new EditableMenu(title, val, new MenuCallback() {
				@Override
				public void menuCommand(String cmd, Object rider) {
					if (!StringUtils.isNullOrEmpty(cmd)) {
						val = cmd;
					}
				}
			}, null);

			return true;
		} else if (key == KeyEvent.VK_DELETE) {
			val = "";
			return true;
		}
		return false;
	}

	@Override
	public void render(Renderer renderer, int yoff, boolean focused) {
		String selectText = "";
		if (focused) {
			selectText = " \001RIGHT\002 to edit, \001DELETE\002 to clear";
		}
		renderInputMessage(renderer, yoff, title + selectText, focused);

		ElementColoring c = UI_TEXT_COLOR;
		if (focused)
			c = UI_SELECT_COLOR;

		renderer.renderText(0, yoff + 1, val.length() + " byte"
				+ ((val.length() != 1) ? "s" : "") + ".", c);

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
