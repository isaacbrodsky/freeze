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
public class TextInput extends MenuUtils implements UIInteraction,
		TypingInteraction {
	private boolean stillAlive, escapePressed, cancellable, blankLast;

	private StringBuilder contents;
	private int loc;
	private String title;
	private MenuCallback callback;

	private boolean submittable;

	private Object rider;

	private int y;

	public TextInput(String title, String data, boolean submittable) {
		this(title, data, null, false, null, 0);
		this.submittable = submittable;
	}

	/**
	 * @param title
	 * @param data
	 * @param callback
	 * @param singleLetter
	 * @param cancellable
	 * @param rider
	 */
	public TextInput(String title, String data, MenuCallback callback,
			boolean cancellable, Object rider) {
		this(title, data, callback, cancellable, rider,
				Renderer.DISPLAY_HEIGHT - 2);
	}

	/**
	 * @param title
	 * @param data
	 * @param callback
	 * @param singleLetter
	 * @param cancellable
	 * @param rider
	 */
	public TextInput(String title, String data, MenuCallback callback,
			boolean cancellable, Object rider, int y) {
		this.submittable = true;
		this.cancellable = cancellable;
		this.stillAlive = true;
		this.escapePressed = false;
		this.title = title;
		this.callback = callback;
		this.rider = rider;

		contents = new StringBuilder(data);

		loc = contents.length();

		this.y = y;
		blankLast = false;
	}

	@Override
	public boolean keyTyped(int key, int mod) {
		if (((mod & KeyEvent.ALT_DOWN_MASK) == KeyEvent.ALT_DOWN_MASK)
				|| key >= 0x20 && key <= 0x80) {
			contents = contents.insert(loc, (char) key);
			loc++;
			return true;
		}
		return false;
	}

	@Override
	public boolean keyPress(int key) {
		boolean consume = false;
		switch (key) {
		// case KeyEvent.VK_RIGHT:
		// loc++;
		// break;
		// case KeyEvent.VK_LEFT:
		// loc--;
		// break;
		// case KeyEvent.VK_HOME:
		// loc = 0;
		// break;
		// case KeyEvent.VK_END:
		// loc = contents.length();
		// break;
		case KeyEvent.VK_ESCAPE:
			escapePressed = true;
			consume = true;
		case KeyEvent.VK_ENTER:
			if (submittable)
				stillAlive = false;
			consume = true;
			break;
		case KeyEvent.VK_BACK_SPACE:
			loc--;
		case KeyEvent.VK_DELETE:
			if (loc < 0)
				loc = 0;
			if (loc >= contents.length())
				loc = contents.length() - 1;
			if (contents.length() >= 1)
				contents.deleteCharAt(loc);
			consume = true;
			blankLast = true;
			break;
		}

		if (loc > contents.length())
			loc = contents.length();
		if (loc < 0)
			loc = 0;

		return consume;
	}

	@Override
	public String getSelectedText() {
		return contents.toString();
	}

	@Override
	public String getSelectedLabel() {
		return contents.toString();
	}

	@Override
	public void render(Renderer renderer, int yoff, boolean focused) {
		renderInputMessage(renderer, y + yoff, title, focused);

		yoff = yoff + title.split("\n").length - 1;

		int x = 0;
		for (int i = 0; i < contents.length(); i++) {
			char s = contents.charAt(i);
			ElementColoring c;
			if (i == loc && focused) {
				c = UI_SELECT_COLOR;
			} else {
				c = UI_TEXT_COLOR;
			}

			renderer.renderText(x, y + 1 + yoff, s, c);
			x++;
		}
		if (loc == x) {
			renderer.renderText(x, y + 1 + yoff, ' ',
					(focused) ? UI_SELECT_COLOR : UI_TEXT_COLOR);
			x++;
		}
		if (blankLast) {
			renderer.renderText(x, y + 1 + yoff, ' ', UI_TEXT_COLOR);
			blankLast = false;
		}
	}

	@Override
	public void tick() {
		if (!stillAlive && submittable) {
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
		if (escapePressed && !submittable)
			return false;
		return stillAlive;
	}

	@Override
	public UIInteraction getFocusedInteraction() {
		return this;
	}
}
