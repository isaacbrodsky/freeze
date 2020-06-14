/**
 * 
 */
package com.isaacbrodsky.freeze2.menus.stateditor;

import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.Stat;
import com.isaacbrodsky.freeze2.game.editor.EditorController;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;
import com.isaacbrodsky.freeze2.graphics.NamedColor;
import com.isaacbrodsky.freeze2.graphics.Renderer;
import com.isaacbrodsky.freeze2.menus.MenuCallback;
import com.isaacbrodsky.freeze2.menus.MenuUtils;
import com.isaacbrodsky.freeze2.menus.TypingInteraction;
import com.isaacbrodsky.freeze2.menus.UIInteraction;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author isaac
 * 
 */
public class StatMultiInput extends MenuUtils implements UIInteraction, TypingInteraction {
	private static final int PER_PAGE = 21;

	private final EditorController editor;
	private final Board board;

	private String title;
	private List<StatInput> ui;
	private int select;
	private int nextStatIndex;

	private MenuCallback callback;

	private boolean stillAlive;

	private String cmd;

	public StatMultiInput(EditorController editor,
						  Board board,
						  String title,
						  int nextStatIndex,
						  List<StatInput> ui,
                          MenuCallback callback
	) {
		if (ui.size() == 0)
			throw new IllegalArgumentException("StatMultiInput cannot be empty");
		this.title = title;
		this.editor = editor;
		this.board = board;
		this.select = 0;
		this.ui = new ArrayList<>(ui);
		this.callback = callback;
		stillAlive = true;
		this.nextStatIndex = nextStatIndex;
	}

	@Override
	public String getSelectedLabel() {
		return null;
	}

	@Override
	public String getSelectedText() {
		return null;
	}

	@Override
	public boolean keyPress(int key) {
		boolean b = false;
		UIInteraction focused = getFocusedInteraction();
		if (focused == this || ui.contains(focused))
			b = bypassKeyPress(key);

		if (b)
			return b;
		return ui.get(select).keyPress(key);
	}

	public boolean bypassKeyPress(int key) {
		switch (key) {
			case KeyEvent.VK_HOME:
				select = 0;
				break;
			case KeyEvent.VK_PAGE_UP:
				select -= PER_PAGE + 1;
			case KeyEvent.VK_UP:
				if (select <= 0)
					select = ui.size() - 1;
				else
					select--;
				return true;
			case KeyEvent.VK_END:
				select = ui.size() - 1;
				break;
			case KeyEvent.VK_PAGE_DOWN:
				select += PER_PAGE - 1;
			case KeyEvent.VK_DOWN:
				if (select >= ui.size() - 1)
					select = 0;
				else
					select++;
				return true;
			case KeyEvent.VK_ESCAPE:
				stillAlive = false;
				cmd = "CANCEL";
				break;
			case KeyEvent.VK_ENTER:
				stillAlive = false;
				cmd = "SUBMIT";
				break;
		}

		return false;
	}

	@Override
	public boolean keyTyped(int key, int mod) {
		UIInteraction focused = getFocusedInteraction();
		if (focused != this && focused instanceof TypingInteraction) {
			return ((TypingInteraction) focused).keyTyped(key, mod);
		}
		// Treated as typing to avoid keyrepeat
		switch (Character.toUpperCase(key)) {
			case 'Q':
				if (select > 0) {
					StatInput tmp = ui.get(select);
					ui.set(select, ui.get(select - 1));
					select--;
					ui.set(select, tmp);
				}
				return true;
			case 'A':
				if (select < ui.size() - 1) {
					StatInput tmp = ui.get(select);
					ui.set(select, ui.get(select + 1));
					select++;
					ui.set(select, tmp);
				}
				return true;
			case 'I':
				Stat stat = new Stat(editor.getCursor().getX(), editor.getCursor().getY());
				boolean foundPlace = false;
				for (int i = 0; i < ui.size(); i++) {
					if (ui.get(i).stat == null) {
						ui.get(i).stat = stat;
						foundPlace = true;
						select = i;
						break;
					}
				}
				if (!foundPlace) {
					nextStatIndex++;
					ui.add(new StatInput(editor, board, nextStatIndex, stat));
					select = ui.size() - 1;
				}
				return true;
			case 'U':
				Stat srcStat = ui.get(select).stat;
				if (srcStat != null) {
					Stat dupStat = new Stat(srcStat);
					ui.add(new StatInput(editor, board, ui.size(), dupStat));
				}
				return true;
		}
		return false;
	}

	private String currPageString() {
		int curr = ((select / PER_PAGE) + 1);

		int pages = (int) Math.ceil((double) ui.size() / (double) PER_PAGE);

		return ((pages > 1) ? ("Page " + curr + "/" + pages + "") : "");
	}

	@Override
	public void render(Renderer renderer, int yoff, boolean focused) {
		// TODO: Refactor this to use MultiInput
		// TODO: Do not hard code the number of lines in different places
		renderer.renderFill(0, 0, Renderer.DISPLAY_WIDTH,
				Renderer.DISPLAY_HEIGHT, ' ', ElementColoring.forCode(0));

		renderInputMessage(renderer, yoff, title, focused);
		renderCenterMessage(renderer, yoff + 3, currPageString(), focused);
		int i = 0;
		int tempselect = select;
		while (tempselect >= PER_PAGE) {
			i += PER_PAGE;
			yoff += PER_PAGE;
			tempselect -= PER_PAGE;
		}
		for (; i < ui.size(); i++) {
			if (i == select) {
				renderer.renderFill(0, i + 4 - yoff,
						Renderer.DISPLAY_WIDTH, 1, ' ', ElementColoring.forNames(
								NamedColor.BLACK, NamedColor.DARKBLUE));
				ui.get(i).render(renderer, i + 4 - yoff, focused);

				for (int x = 0; x < Renderer.DISPLAY_WIDTH; x++) {
					for (int y = i + 4 - yoff; y < i + 5 - yoff
							&& y < Renderer.DISPLAY_HEIGHT; y++) {
						GraphicsBlock b = renderer.get(x, y);
						if (b.getColoring().getBackCode() == 0x00) {
							b = new GraphicsBlock(b.getColoring().deriveBack(
									0x01), b.getCharIndex());
						}
						if (b.getColoring().getForeCode() == 0x00) {
							b = new GraphicsBlock(b.getColoring().deriveFore(
									0x01), b.getCharIndex());
						}
						renderer.set(x, y, b);
					}
				}
			} else {
				ui.get(i).render(renderer, i + 4 - yoff, false);
			}
		}

		if (getFocusedInteraction() != ui.get(select)) {
			// TODO: Redraw the selected item here (in case it is taking over the screen)
			ui.get(select).render(renderer, select + 4 - yoff, focused);
//			getFocusedInteraction().render(renderer, select + 4 - yoff,
//					true);
		}
	}

	@Override
	public boolean stillAlive() {
		return stillAlive;
	}

	@Override
	public void tick() {
		ui.get(select).tick();
		if (!stillAlive) {
			if (callback != null) {
				List<Stat> results = ui.stream()
						.map(i -> i.stat)
						.collect(Collectors.toList());

				callback.menuCommand(cmd, results);
			}
		}
	}

	@Override
	public UIInteraction getFocusedInteraction() {
		return ui.get(select).getFocusedInteraction();
	}
}
