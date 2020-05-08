/**
 * 
 */
package com.isaacbrodsky.freeze2.menus;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;
import com.isaacbrodsky.freeze2.graphics.NamedColor;
import com.isaacbrodsky.freeze2.graphics.Renderer;

/**
 * @author isaac
 * 
 */
public class MultiInput extends MenuUtils implements UIInteraction {
	private static final int PER_PAGE = 11;

	private String title;
	private List<UIInteraction> ui;
	private List<UIInteraction> reset;
	private int select;

	private MenuCallback<List<String>> callback;

	private boolean stillAlive;

	private String cmd;

	public MultiInput(String title, List<UIInteraction> ui,
			MenuCallback<List<String>> callback) {
		if (ui.size() == 0)
			throw new IllegalArgumentException("MultiInput cannot be empty");
		this.title = title;
		this.reset = new ArrayList<>(ui);
		this.callback = callback;
		stillAlive = true;

		resetUI();
	}

	private void resetUI() {
		select = 0;
		ui = new ArrayList<>(reset);
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

	private String currPageString() {
		int curr = ((select / PER_PAGE) + 1);

		int pages = (int) Math.ceil((double) ui.size() / (double) PER_PAGE);

		return ((pages > 1) ? ("Page " + curr + "/" + pages + "") : "");
	}

	@Override
	public void render(Renderer renderer, int yoff, boolean focused) {
		renderer.renderFill(0, 0, Renderer.DISPLAY_WIDTH,
				Renderer.DISPLAY_HEIGHT, ' ', ElementColoring.forCode(0));

		renderInputMessage(renderer, yoff, title, focused);
		renderCenterMessage(renderer, yoff + 2, currPageString(), focused);
		int i = 0;
		int tempselect = select;
		while (tempselect >= PER_PAGE) {
			i += PER_PAGE;
			yoff += PER_PAGE * 2;
			tempselect -= PER_PAGE;
		}
		for (; i < ui.size(); i++) {
			if (i == select) {
				renderer.renderFill(0, (i * 2) + 3 - yoff,
						Renderer.DISPLAY_WIDTH, 2, ' ', ElementColoring.forNames(
								NamedColor.BLACK, NamedColor.DARKBLUE));
				ui.get(i).render(renderer, (i * 2) + 3 - yoff, focused);

				for (int x = 0; x < Renderer.DISPLAY_WIDTH; x++) {
					for (int y = (i * 2) + 3 - yoff; y < (i * 2) + 5 - yoff
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
				ui.get(i).render(renderer, (i * 2) + 3 - yoff, false);
			}
		}

		if (getFocusedInteraction() != ui.get(select)) {
			getFocusedInteraction().render(renderer, (select * 2) + 3 - yoff,
					true);
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
				List<String> results = ui.stream()
						.map(UIInteraction::getSelectedLabel)
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
