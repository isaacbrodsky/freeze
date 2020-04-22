/**
 * 
 */
package com.isaacbrodsky.freeze.menus;

import java.awt.event.KeyEvent;

import com.isaacbrodsky.freeze.graphics.Renderer;

/**
 * @author isaac
 * 
 */
public class CharInput extends MenuUtils implements UIInteraction,
		TypingInteraction {
	private boolean stillAlive, escapePressed, cancellable, useCodes;

	private int typed;

	private String title;
	private MenuCallback callback;

	private Object rider;

	private int y;

	public CharInput(String title, MenuCallback callback, boolean cancellable,
			Object rider) {
		this(title, callback, cancellable, rider, Renderer.DISPLAY_HEIGHT - 2);
	}

	public CharInput(String title, MenuCallback callback, boolean cancellable,
			Object rider, int y) {
		this(title, callback, cancellable, rider, y, true);
	}
	
	public CharInput(String title, MenuCallback callback, boolean cancellable,
			Object rider, int y, boolean useCodes) {
		this.cancellable = cancellable;
		this.stillAlive = true;
		this.escapePressed = false;
		this.title = title;
		this.callback = callback;
		this.rider = rider;
		this.useCodes = useCodes;
		
		this.y = y;
	}

	@Override
	public boolean keyTyped(int key, int mod) {
		if (((mod & KeyEvent.ALT_DOWN_MASK) == KeyEvent.ALT_DOWN_MASK)
				|| key >= 0x20 && key <= 0x80) {
			typed = key;
			stillAlive = false;
			return true;
		}
		return false;
	}

	@Override
	public boolean keyPress(int key) {
		if (key == KeyEvent.VK_ESCAPE) {
			escapePressed = true;
			return true;
		}
		if (key >= 0x20 && key <= 0x80) {
			return true;
		}

		return false;
	}

	@Override
	public String getSelectedText() {
		return Character.toString((char) typed);
	}

	@Override
	public String getSelectedLabel() {
		return Character.toString((char) typed);
	}

	@Override
	public void render(Renderer renderer, int yoff, boolean focused) {
		renderInputMessage(renderer, y + yoff, title, focused, useCodes);

		// yoff = yoff + title.split("\n").length - 1;
		//
		// renderer.renderText(0, y + 1 + yoff, ' ', (focused) ? UI_SELECT_COLOR
		// : UI_TEXT_COLOR);
	}

	@Override
	public void tick() {
		if (!stillAlive) {
			if (callback != null) {
				if (escapePressed && cancellable) {
					callback.menuCommand(null, rider);
				} else {
					if (getSelectedLabel() != null)
						callback.menuCommand(getSelectedLabel(), rider);
				}
			}
		}
	}

	@Override
	public boolean stillAlive() {
		if (escapePressed)
			return false;
		return stillAlive;
	}

	@Override
	public UIInteraction getFocusedInteraction() {
		return this;
	}
}
