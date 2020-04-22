/**
 * 
 */
package com.isaacbrodsky.freeze.menus;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.isaacbrodsky.freeze.graphics.ElementColoring;
import com.isaacbrodsky.freeze.graphics.Renderer;

/**
 * @author isaac
 * 
 */
public class Menu extends MenuUtils implements UIInteraction {

	public static final int MAX_LINE_LENGTH = 41;
	
	private boolean stillAlive, escapePressed, cancellable;

	private ArrayList<MenuItem> contents;
	private int topPos, loc;
	private String title;
	private MenuCallback callback;
	private SendMode sendLast;

	private Object rider;

	public static enum SendMode {
		NO, LASTLINE, LASTNO
	}

	Menu() {

	}

	public Menu(String title, String data, MenuCallback callback) {
		this(title, data, callback, SendMode.NO);
	}

	/**
	 * @param string
	 * @param string2
	 * @param zApplet
	 * @param b
	 */
	public Menu(String title, String data, MenuCallback callback,
			SendMode sendLast) {
		this(title, data, callback, sendLast, false);
	}

	public Menu(String title, String data, MenuCallback callback,
			SendMode sendLast, boolean cancellable) {
		this(title, data, callback, sendLast, cancellable, null);
	}

	public Menu(String title, String data, MenuCallback callback,
			SendMode sendLast, boolean cancellable, Object rider) {
		this.cancellable = cancellable;
		this.stillAlive = true;
		this.escapePressed = false;
		this.title = title;
		this.callback = callback;
		loc = 0;
		this.sendLast = sendLast;
		this.rider = rider;
		topPos = loc - (PAGE_HEIGHT / 2);

		String lines[] = data.split("(\\r?\\n|\\r)");
		contents = new ArrayList<MenuItem>(lines.length);

		for (int i = 0; i < lines.length; i++) {
			MenuItem.ItemType type = MenuItem.ItemType.NORMAL;
			String label = null;
			if (lines[i].length() > 0) {
				char c = lines[i].charAt(0);
				if (c == '$') {
					lines[i] = lines[i].substring(1);
					type = MenuItem.ItemType.CENTERED;
				} else if (c == '!') {
					lines[i] = lines[i].substring(1);
					String[] comps = lines[i].split(";", 2);
					if (comps.length == 2) {
						label = comps[0];
						lines[i] = comps[1];
					} else {
						lines[i] = "!" + lines[i];
					}
					type = MenuItem.ItemType.SELECTABLE;
				}
			}

			contents.add(new MenuItem(lines[i], type, label));
		}
	}

	@Override
	public boolean keyPress(int key) {
		boolean consume = false;
		switch (key) {
		case KeyEvent.VK_DOWN:
			loc++;
			consume = true;
			break;
		case KeyEvent.VK_UP:
			loc--;
			consume = true;
			break;
		case KeyEvent.VK_HOME:
			loc = 0;
			consume = true;
			break;
		case KeyEvent.VK_END:
			loc = contents.size() - 1;
			consume = true;
			break;
		case KeyEvent.VK_PAGE_DOWN:
			loc += PAGE_HEIGHT;
			consume = true;
			break;
		case KeyEvent.VK_PAGE_UP:
			loc -= PAGE_HEIGHT;
			consume = true;
			break;
		case KeyEvent.VK_ESCAPE:
			escapePressed = true;
			// fall through
		case KeyEvent.VK_ENTER:
			stillAlive = false;
			consume = true;
			break;
		}

		if (loc >= contents.size())
			loc = contents.size() - 1;
		if (loc < 0)
			loc = 0;

		topPos = loc - (PAGE_HEIGHT / 2);
		// topPos = loc - (PAGE_HEIGHT / 2);
		// if (topPos + PAGE_HEIGHT > contents.size())
		// topPos = contents.size() - PAGE_HEIGHT;
		// if (topPos < 0)
		// topPos = 0;

		return consume;
	}

	@Override
	public String getSelectedText() {
		return ((contents.get(loc).type == MenuItem.ItemType.CENTERED) ? '$'
				: "")
				+ contents.get(loc).data;
	}

	@Override
	public String getSelectedLabel() {
		if (contents.get(loc).type.equals(MenuItem.ItemType.SELECTABLE)) {
			return contents.get(loc).label;
		}

		return null;
	}

	@Override
	public void render(Renderer renderer, int yoff, boolean focused) {
		int offsetX = 7, offsetY = 4;
		renderer.renderFill(offsetX, offsetY, PAGE_WIDTH + 3, PAGE_HEIGHT + 2,
				' ', MENU_TEXT_COLOR);

		drawBorders(offsetX, offsetY, renderer);

		if (getSelectedLabel() == null) {
			renderer.renderText(centerText(title, PAGE_WIDTH + 1) + offsetX,
					offsetY, title, MENU_TEXT_COLOR);
		} else {
			renderer.renderText(centerText(SELECT_TEXT, PAGE_WIDTH + 1)
					+ offsetX, offsetY, SELECT_TEXT, MENU_TEXT_COLOR);
		}
		offsetX++;
		for (int i = topPos; i < PAGE_HEIGHT + topPos; i++) {
			int height = (i + 2) - topPos;

			if (i == -1) {
				renderer.renderText(offsetX - 1, height + offsetY,
						SEPERATOR_LINE, MENU_TEXT_COLOR);
				continue;
			}
			if (i < 0)
				continue;
			if (contents.size() == i) {
				renderer.renderText(offsetX - 1, height + offsetY,
						SEPERATOR_LINE, MENU_TEXT_COLOR);
				continue;
			}
			if (contents.size() < i)
				break;

			MenuItem item = contents.get(i);
			switch (item.type) {
			case CENTERED:
				int offset = 1 + centerText(item.data, PAGE_WIDTH);
				renderer.renderText(offset + offsetX, height + offsetY,
						item.data, MENU_ALT_COLOR);
				break;
			case SELECTABLE:
				renderer.renderText(3 + offsetX, height + offsetY, 16,
						new ElementColoring("PURPLE", "DARKBLUE"));
				renderer.renderText(5 + offsetX, height + offsetY, item.data,
						MENU_ALT_COLOR);
				break;
			case NORMAL:
			default:
				renderer.renderText(1 + offsetX, height + offsetY, item.data,
						MENU_TEXT_COLOR);
			}
		}

		if (focused) {
			renderer.renderText(offsetX - 1, loc - topPos + 2 + offsetY, 175,
					MENU_POSARROW_COLOR);
			renderer.renderText(PAGE_WIDTH + 1 + offsetX, loc - topPos + 2
					+ offsetY, 174, MENU_POSARROW_COLOR);
		}
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
					else if (sendLast == SendMode.LASTLINE)
						callback.menuCommand(getSelectedText(), rider);
					else if (sendLast == SendMode.LASTNO)
						callback.menuCommand(Integer.toString(loc), rider);
				}
			}
		}
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
