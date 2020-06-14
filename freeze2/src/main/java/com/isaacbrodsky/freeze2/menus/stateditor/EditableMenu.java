/**
 * 
 */
package com.isaacbrodsky.freeze2.menus.stateditor;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.isaacbrodsky.freeze2.ZTestMain;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.NamedColor;
import com.isaacbrodsky.freeze2.graphics.Renderer;
import com.isaacbrodsky.freeze2.menus.Menu;
import com.isaacbrodsky.freeze2.menus.MenuCallback;
import com.isaacbrodsky.freeze2.menus.TypingInteraction;
import com.isaacbrodsky.freeze2.menus.UIInteraction;
import com.isaacbrodsky.freeze2.oop.OopUtils;

/**
 * @author isaac
 * 
 */
public class EditableMenu<T> extends Menu implements TypingInteraction {

	private boolean stillAlive;

	private ArrayList<StringBuilder> contents;
	private int topPos, loc, lineLoc;
	private String title;
	private MenuCallback<T> callback;
	private UIInteraction submenu;

	private T rider;

	public EditableMenu(String title, String data, MenuCallback<T> callback, T rider) {
		this.stillAlive = true;
		this.title = title;
		this.callback = callback;
		loc = lineLoc = 0;
		this.rider = rider;
		topPos = loc - (PAGE_HEIGHT / 2);
		this.submenu = null;

		String lines[] = data.split("(\\r?\\n|\\r)");
		contents = new ArrayList<>(lines.length);

		for (int i = 0; i < lines.length; i++) {
			contents.add(new StringBuilder(lines[i]));
		}
	}

	@Override
	public boolean keyTyped(int key, int mod) {
		if (submenu != null) {
			return false;
		}
		return insertCharacter(key, mod, false);
	}

	private boolean insertCharacter(int key, int mod, boolean force) {
		if (contents.get(loc).length() >= MAX_LINE_LENGTH) {
			return false;
		}
		boolean keyPermitted = ((mod & KeyEvent.ALT_DOWN_MASK) == KeyEvent.ALT_DOWN_MASK)
				|| key >= 0x20 && key < 0x7F;
		if (keyPermitted || force) {
			contents.get(loc).insert(lineLoc, (char) key);
			lineLoc++;
			return true;
		}
		return false;
	}

	@Override
	public boolean keyPress(int key) {
		if (submenu != null)
			return submenu.keyPress(key);

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
		case KeyEvent.VK_F3:
			submenu = ZTestMain.generateASCIITable(false);
			break;
		case KeyEvent.VK_INSERT:
			submenu = new MatrixInputForm("Select character", 0, MatrixInput.makeCharList(), (cmd, rider) -> {
				if ("SUBMIT".equals(cmd)) {
					insertCharacter(rider, 0, true);
				}
			});
			break;
		case KeyEvent.VK_BACK_SPACE:
			lineLoc--;
		case KeyEvent.VK_DELETE:
			// TODO: This merge delete behavior can result in lines that are too long
			boolean doDelete = true;
			if (lineLoc < 0) {
				// Back spacing to next line
				lineLoc = 0;
				if (loc > 0) {
					String s = contents.remove(loc).toString();
					loc--;
					lineLoc = contents.get(loc).length();
					contents.get(loc).append(s);
				}
				break;
			} else if (lineLoc == contents.get(loc).length()) {
				// Deleting to merge next line
				if (loc + 1 < contents.size()) {
					String s = contents.remove(loc + 1).toString();
					contents.get(loc).append(s);
					doDelete = false;
				}
			}
			if (doDelete && contents.get(loc).length() > lineLoc)
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

			renderOopLine(renderer, 1 + offsetX, height + offsetY, contents.get(i).toString());
			if (i == loc) {
				// render current line
				if (lineLoc < contents.get(i).length()) {
					renderer.renderText(1 + offsetX + lineLoc, height + offsetY,
							contents.get(i).charAt(lineLoc),
							UI_SELECT_COLOR);
				} else if (lineLoc == contents.get(i).length()) {
					char cursorChar = ' ';
					if (lineLoc == MAX_LINE_LENGTH)
						cursorChar = 176; // invert
					renderer.renderText(1 + offsetX + contents.get(i).length(),
							height + offsetY, cursorChar, UI_SELECT_COLOR);
				}
			}
		}

		if (focused) {
			renderer.renderText(offsetX - 1, loc - topPos + 2 + offsetY, 175,
					MENU_POSARROW_COLOR);
			renderer.renderText(PAGE_WIDTH + 1 + offsetX, loc - topPos + 2
					+ offsetY, 174, MENU_POSARROW_COLOR);
		}
		if (submenu != null) {
			submenu.render(renderer, 0, focused);
		}
	}

	// Syntax highlighting
	private static final ElementColoring COLOR_GRAMMAR = ElementColoring.forNames(NamedColor.WHITE, NamedColor.DARKBLUE);
	private static final ElementColoring COLOR_NAME = ElementColoring.forNames(NamedColor.WHITE, NamedColor.DARKBLUE);
	private static final ElementColoring COLOR_LABEL = ElementColoring.forNames(NamedColor.CYAN, NamedColor.DARKBLUE);
	private static final ElementColoring COLOR_COMMENT = ElementColoring.forNames(NamedColor.GRAY, NamedColor.DARKBLUE);
	private static final ElementColoring COLOR_NULL = ElementColoring.forNames(NamedColor.RED, NamedColor.DARKBLUE);
	private static final ElementColoring COLOR_COMMAND = ElementColoring.forNames(NamedColor.GREEN, NamedColor.DARKBLUE);

	private void renderOopLine(Renderer renderer, int x, int y, String text) {
		if (text.startsWith("@")) {
			renderer.renderText(x, y, text, COLOR_GRAMMAR);
			renderer.renderText(x + 1, y, text.substring(1), COLOR_NAME);
		} else if (text.startsWith(":")) {
			renderer.renderText(x, y, text, COLOR_LABEL);
		} else if (text.startsWith("'")) {
			renderer.renderText(x, y, text, COLOR_COMMENT);
		} else if (text.startsWith("/")) {
			renderer.renderText(x, y, '/', COLOR_GRAMMAR);
			// TODO: Validate the word
			renderer.renderText(x + 1, y, text.substring(1), COLOR_COMMAND);
			int wordLength = OopUtils.readWord(text, 1).length();
			renderOopLine(renderer, x + wordLength + 1, y, text.substring(wordLength + 1));
		} else if (text.startsWith("?")) {
			renderer.renderText(x, y, '?', COLOR_GRAMMAR);
			// TODO: Validate the word
			renderer.renderText(x + 1, y, text.substring(1), COLOR_COMMAND);
			int wordLength = OopUtils.readWord(text, 1).length();
			renderOopLine(renderer, x + wordLength + 1, y, text.substring(wordLength + 1));
		} else if (text.startsWith("#")) {
			// TODO, do this better
			renderer.renderText(x, y, '#', COLOR_GRAMMAR);
			renderer.renderText(x + 1, y, text.substring(1), COLOR_COMMAND);
		} else if (text.startsWith("!")) {
			int semicolonIdx = text.indexOf(';');
			renderer.renderText(x, y, '!', COLOR_GRAMMAR);
			if (semicolonIdx != -1) {
				renderer.renderText(x + 1, y, text.substring(1, semicolonIdx), COLOR_LABEL);
				renderer.renderText(x + semicolonIdx, y, ';', COLOR_GRAMMAR);
				renderer.renderText(x + semicolonIdx + 1, y, text.substring(semicolonIdx + 1), MENU_ALT_COLOR);
			} else {
				renderer.renderText(x + 1, y, text.substring(1), COLOR_LABEL);
			}
		} else if (text.startsWith("$")) {
			renderer.renderText(x, y, text, MENU_ALT_COLOR);
		} else {
			renderer.renderText(x, y, text, MENU_TEXT_COLOR);
		}
		int nullIdx = text.indexOf(0);
		while (nullIdx != -1) {
			renderer.renderText(x + nullIdx, y, 219, COLOR_NULL);

			nullIdx = text.indexOf(0, nullIdx + 1);
		}
	}

	@Override
	public void tick() {
		if (submenu != null) {
			submenu.tick();
			if (!submenu.stillAlive())
				submenu = null;
		}
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
		if (contents.size() == 1 && contents.get(0).length() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < contents.size(); i++) {
			sb.append(contents.get(i)).append('\r');
		}
		return sb.toString();
	}
}
