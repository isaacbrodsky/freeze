/**
 * 
 */
package com.isaacbrodsky.freeze2.menus.stateditor;

import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;
import com.isaacbrodsky.freeze2.graphics.Renderer;
import com.isaacbrodsky.freeze2.menus.MenuCallback;
import com.isaacbrodsky.freeze2.menus.MenuUtils;
import com.isaacbrodsky.freeze2.menus.UIInteraction;
import com.isaacbrodsky.freeze2.utils.TimeAndMathUtils;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author isaac
 * 
 */
public class MatrixInput extends MenuUtils implements UIInteraction {
	private final List<GraphicsBlock> options;

	private String title;
	private int val;
	private UIInteraction select;

	public MatrixInput(String title, int curr, List<GraphicsBlock> options) {
		select = null;
		this.title = title;
		this.val = curr;
		this.options = options;
	}

	@Override
	public String getSelectedLabel() {
		return Integer.toString(val);
	}

	@Override
	public String getSelectedText() {
		return Integer.toString(val);
	}

	@Override
	public boolean keyPress(int key) {
		if (select != null)
			return select.keyPress(key);
		else if (key == KeyEvent.VK_RIGHT) {
			// TODO: Don't use VK_RIGHT
			select = new MatrixInputForm(title, val, options, (cmd, rider) -> {
				if ("SUBMIT".equals(cmd)) {
					val = rider;
				}
			});

			return true;
		}
		return false;
	}

	@Override
	public void render(Renderer renderer, int yoff, boolean focused) {
		String selectText = "";
		if (focused) {
			selectText = " \001RIGHT\002 to select";
		}
		renderInputMessage(renderer, yoff, title + selectText, focused);

		ElementColoring c = UI_TEXT_COLOR;
		if (focused)
			c = UI_SELECT_COLOR;

		renderer.renderText(0, yoff + 1, TimeAndMathUtils.padInt(val, 2, '0', 16), c);

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

	public static List<GraphicsBlock> makeCharList() {
		return IntStream.rangeClosed(0, 0xFF)
				.mapToObj(i -> new GraphicsBlock(0x0F, i))
				.collect(Collectors.toList());
	}

	public static List<GraphicsBlock> makeColorList() {
		return IntStream.rangeClosed(0, 0xFF)
				.mapToObj(i -> new GraphicsBlock(i, 'A'))
				.collect(Collectors.toList());
	}
}
