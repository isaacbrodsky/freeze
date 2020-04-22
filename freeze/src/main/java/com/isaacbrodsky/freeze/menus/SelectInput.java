/**
 * 
 */
package com.isaacbrodsky.freeze.menus;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.isaacbrodsky.freeze.graphics.ElementColoring;
import com.isaacbrodsky.freeze.graphics.Renderer;

/**
 * @author isaac
 * 
 */
public class SelectInput extends MenuUtils implements UIInteraction {
	private boolean stillAlive, escapePressed, cancellable;

	private ArrayList<String> contents;
	private int loc;
	private String title;
	private MenuCallback callback;
	private boolean singleLetter;

	private int y;

	private Object rider;

	private boolean submittable;

	public SelectInput(String title2, String[] strings, boolean singleLetter,
			int selected, boolean submittable) {
		this(title2, Arrays.asList(strings), null, singleLetter, false, null,
				selected, 0);
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
	public SelectInput(String title, List<String> data, MenuCallback callback,
			boolean singleLetter, boolean cancellable, Object rider,
			int selected, int y) {
		this.submittable = true;
		this.cancellable = cancellable;
		this.stillAlive = true;
		this.escapePressed = false;
		this.title = title;
		this.callback = callback;
		loc = selected;
		this.singleLetter = singleLetter;
		this.rider = rider;

		this.y = y;

		contents = new ArrayList<String>(data);
	}

	/**
	 * @param title2
	 * @param strings
	 * @param callback2
	 * @param sendLast2
	 * @param cancellable2
	 * @param rider2
	 */
	public SelectInput(String title2, String[] strings, MenuCallback callback2,
			boolean singleLetter, boolean cancellable2, Object rider2,
			int selected) {
		this(title2, Arrays.asList(strings), callback2, singleLetter,
				cancellable2, rider2, selected, Renderer.DISPLAY_HEIGHT - 2);
	}

	@Override
	public boolean keyPress(int key) {
		boolean consume = false;
		switch (key) {
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_RIGHT:
			loc++;
			break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_LEFT:
			loc--;
			break;
		case KeyEvent.VK_HOME:
			loc = 0;
			break;
		case KeyEvent.VK_END:
			loc = contents.size() - 1;
			break;
		case KeyEvent.VK_ESCAPE:
			escapePressed = true;
			consume = true;
		case KeyEvent.VK_ENTER:
			if (submittable)
				stillAlive = false;
			consume = true;
			break;

		default:
			if (singleLetter) {
				for (int i = 0; i < contents.size(); i++) {
					String s = contents.get(i);
					char sc = s.toLowerCase().charAt(0);
					if (sc == Character.toLowerCase(key)) {
						loc = i;
						stillAlive = false;
					}
					consume = true;
				}
			}
			break;
		}

		if (loc >= contents.size())
			loc = contents.size() - 1;
		if (loc < 0)
			loc = 0;

		return consume;
	}

	@Override
	public String getSelectedText() {
		return contents.get(loc);
	}

	@Override
	public String getSelectedLabel() {
		return contents.get(loc);
	}

	@Override
	public void render(Renderer renderer, int yoff, boolean focused) {
		renderInputMessage(renderer, y + yoff, title, focused);

		int x = 0;
		for (int i = 0; i < contents.size(); i++) {
			String s = contents.get(i);
			ElementColoring c;
			if (i == loc) {
				if (focused)
					c = UI_SELECT_COLOR;
				else
					c = UI_TEXT_COLOR;
			} else {
				if (focused)
					c = UI_TEXT_COLOR;
				else
					c = UI_BLANK_COLOR;
			}

			renderer.renderText(x, y + 1 + yoff, s, c);
			x += s.length() + 1;
			if (i < contents.size() - 1) {
				renderer.renderText(x - 1, y + 1 + yoff, ' ', UI_TEXT_COLOR);
			}
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
