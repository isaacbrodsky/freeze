/**
 * 
 */
package com.isaacbrodsky.freeze.menus;

import java.awt.event.KeyEvent;

import com.isaacbrodsky.freeze.graphics.ElementColoring;
import com.isaacbrodsky.freeze.graphics.Renderer;

/**
 * @author isaac
 * 
 */
public class EditorInput extends MenuUtils implements TypingInteraction {
	private String title;
	private String val;
	private TypingInteraction select;

	/**
	 * @param title
	 * @param curr
	 * @param zEditorController
	 * @param b
	 */
	public EditorInput(String title, String curr) {
		select = null;
		this.title = title;
		this.val = curr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.menus.UIInteraction#getSelectedLabel()
	 */
	@Override
	public String getSelectedLabel() {
		return val;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.menus.UIInteraction#getSelectedText()
	 */
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
			select = new EditableMenu(title, val, new MenuCallback() {
				@Override
				public void menuCommand(String cmd, Object rider) {
					if (cmd != null) {
						val = cmd;
					}
				}
			}, null);

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
			selectText = " \001RIGHT\002 to edit";
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
