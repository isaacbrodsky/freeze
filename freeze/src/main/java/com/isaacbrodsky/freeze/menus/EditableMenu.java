/**
 * 
 */
package com.isaacbrodsky.freeze.menus;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.isaacbrodsky.freeze.graphics.Renderer;

/**
 * @author isaac
 * 
 */
public class EditableMenu extends Menu implements UIInteraction,
		TypingInteraction {

	private boolean stillAlive;

	private ArrayList<StringBuilder> contents;
	private int topPos, loc, lineLoc;
	private String title;
	private MenuCallback callback;

	private Object rider;

	public EditableMenu(String title, String data, MenuCallback callback,
			Object rider) {
		this.stillAlive = true;
		this.title = title;
		this.callback = callback;
		loc = lineLoc = 0;
		this.rider = rider;
		topPos = loc - (PAGE_HEIGHT / 2);

		String lines[] = data.split("(\\r?\\n|\\r)");
		contents = new ArrayList<StringBuilder>(lines.length);

		for (int i = 0; i < lines.length; i++) {
			contents.add(new StringBuilder(lines[i]));
		}
	}

	@Override
	public boolean keyTyped(int key, int mod) {
		if (contents.get(loc).length() >= MAX_LINE_LENGTH) {
			return false;
		}
		if (((mod & KeyEvent.ALT_DOWN_MASK) == KeyEvent.ALT_DOWN_MASK)
				|| key >= 0x20 && key <= 0x80) {
			contents.get(loc).insert(lineLoc, (char) key);
			lineLoc++;
			return true;
		}
		return false;
	}

	@Override
	public boolean keyPress(int key) {
		boolean consume = false;
		switch (key) {
		case KeyEvent.VK_HOME:
			lineLoc = 0;
			consume = true;
			break;
		case KeyEvent.VK_END:
			lineLoc = contents.get(loc).length();
			consume = true;
			break;
		case KeyEvent.VK_DOWN:
			loc++;
			if (loc >= contents.size())
				loc = contents.size() - 1;
			verifyLineLoc(false);
			consume = true;
			break;
		case KeyEvent.VK_PAGE_DOWN:
			loc += PAGE_HEIGHT;
			if (loc >= contents.size())
				loc = contents.size() - 1;
			verifyLineLoc(false);
			consume = true;
			break;
		case KeyEvent.VK_PAGE_UP:
			loc -= PAGE_HEIGHT;
			if (loc < 0)
				loc = 0;
			verifyLineLoc(false);
			consume = true;
			break;
		case KeyEvent.VK_UP:
			loc--;
			if (loc < 0)
				loc = 0;
			verifyLineLoc(false);
			consume = true;
			break;
		case KeyEvent.VK_RIGHT:
			lineLoc++;
			verifyLineLoc(true);
			consume = true;
			break;
		case KeyEvent.VK_LEFT:
			lineLoc--;
			verifyLineLoc(true);
			consume = true;
			break;
		case KeyEvent.VK_ESCAPE:
			stillAlive = false;
			consume = true;
			break;
		case KeyEvent.VK_BACK_SPACE:
			lineLoc--;
			// case KeyEvent.VK_DELETE:
			if (lineLoc < 0) {
				lineLoc = 0;
				if (loc > 0) {
					String s = contents.remove(loc).toString();
					loc--;
					lineLoc = contents.get(loc).length();
					contents.get(loc).append(s);
				}
				break;
			}
			if (contents.get(loc).length() >= 1)
				contents.get(loc).deleteCharAt(lineLoc);
			consume = true;
			break;
		case KeyEvent.VK_ENTER:
			loc++;
			if (lineLoc == 0) {
				contents.add(Math.max(loc - 1, 0), new StringBuilder());
			} else if (lineLoc == contents.get(loc - 1).length()) {
				contents.add(loc, new StringBuilder());
			} else {
				String s = contents.remove(loc - 1).toString();
				contents.add(loc - 1,
						new StringBuilder(s.substring(0, lineLoc)));
				contents.add(loc, new StringBuilder(s.substring(lineLoc)));
			}
			lineLoc = 0;
			consume = true;
			break;
		}

		topPos = loc - (PAGE_HEIGHT / 2);

		return consume;
	}

	/**
	 * @param changeLoc
	 */
	private void verifyLineLoc(boolean changeLoc) {
		if (changeLoc) {
			if (lineLoc > contents.get(loc).length()) {
				if (contents.size() - 1 > loc) {
					loc++;
					lineLoc = 0;
				} else {
					lineLoc--;
				}
			} else if (lineLoc < 0) {
				if (loc > 0) {
					loc--;
					lineLoc = contents.get(loc).length();
				} else {
					lineLoc++;
				}
			}
		} else {
			if (lineLoc > contents.get(loc).length()) {
				lineLoc = contents.get(loc).length();
			}
		}
	}

	@Override
	public String getSelectedText() {
		return toString();
	}

	@Override
	public String getSelectedLabel() {
		return toString();
	}

	@Override
	public void render(Renderer renderer, int yoff, boolean focused) {
		int offsetX = 7, offsetY = 4;
		renderer.renderFill(offsetX, offsetY, PAGE_WIDTH + 3, PAGE_HEIGHT + 2,
				' ', MENU_TEXT_COLOR);

		drawBorders(offsetX, offsetY, renderer);

		renderer.renderText(centerText(title + " (ESC to close)",
				PAGE_WIDTH + 1)
				+ offsetX, offsetY, title + " (ESC to close)", MENU_TEXT_COLOR);
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

			if (i == loc) {
				// render current line
				for (int k = 0; k < contents.get(i).length(); k++) {
					renderer.renderText(1 + offsetX + k, height + offsetY,
							contents.get(i).charAt(k),
							(k == lineLoc) ? UI_SELECT_COLOR : MENU_TEXT_COLOR);
				}
				if (lineLoc == contents.get(i).length()) {
					char cursorChar = ' ';
					if (lineLoc == MAX_LINE_LENGTH)
						cursorChar = 176; // invert
					renderer.renderText(1 + offsetX + contents.get(i).length(),
							height + offsetY, cursorChar, UI_SELECT_COLOR);
				}
			} else {
				renderer.renderText(1 + offsetX, height + offsetY, contents
						.get(i).toString(), MENU_TEXT_COLOR);
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
				callback.menuCommand(toString(), rider);
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < contents.size(); i++) {
			if (i != 0)
				sb.append('\r');
			sb.append(contents.get(i));
		}
		return sb.toString();
	}
}
