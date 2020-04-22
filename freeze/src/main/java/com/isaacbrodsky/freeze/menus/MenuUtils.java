/**
 * 
 */
package com.isaacbrodsky.freeze.menus;

import com.isaacbrodsky.freeze.ZGame;
import com.isaacbrodsky.freeze.game.ZBoard;
import com.isaacbrodsky.freeze.graphics.ElementColoring;
import com.isaacbrodsky.freeze.graphics.Renderer;

/**
 * @author isaac
 * 
 */
public class MenuUtils {
	public static final ElementColoring MENU_POSARROW_COLOR = new ElementColoring(
			"RED", "DARKBLUE");
	public static final ElementColoring MENU_ALT_COLOR = new ElementColoring(
			"WHITE", "DARKBLUE");
	public static final ElementColoring MENU_TEXT_COLOR = new ElementColoring(
			"YELLOW", "DARKBLUE");
	public static final ElementColoring MENU_BORDER_COLOR = new ElementColoring(
			"WHITE", "BLACK");

	public static final ElementColoring UI_TEXT_COLOR = new ElementColoring(
			"WHITE", "BLACK");
	public static final ElementColoring UI_SELECT_COLOR = new ElementColoring(
			"BLACK", "WHITE");
	public static final ElementColoring UI_BLANK_COLOR = new ElementColoring(
			"BLACK", "BLACK");

	public static final int PAGE_HEIGHT = 15;
	public static final int PAGE_WIDTH = 42;

	public static final String SEPERATOR_LINE;
	static {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < PAGE_WIDTH; i += 5) {
			sb.append("    ");
			sb.append((char) 0x07);
		}

		SEPERATOR_LINE = sb.toString();
	}

	public static final String SELECT_TEXT = ((char) 174) + " ENTER "
			+ ((char) 175) + " to select";

	/**
	 * @param s
	 * @param width
	 * @return
	 */
	public static int centerText(String s, int width) {
		if (!(s.length() >= width)) {
			int center = (int) Math.floor(width / 2.0d);
			center -= (int) Math.floor(s.length() / 2.0d);
			return center;
		}

		return 0;
	}

	/**
	 * @param offsetX
	 * @param offsetY
	 * @param renderer
	 */
	public static void drawBorders(int offsetX, int offsetY, Renderer renderer) {
		renderer.renderText(offsetX - 2, offsetY - 1, 198, MENU_BORDER_COLOR);
		renderer.renderText(offsetX - 1, offsetY - 1, 209, MENU_BORDER_COLOR);
		renderer.renderFill(offsetX, offsetY - 1, PAGE_WIDTH + 3, 1,
				(char) 205, MENU_BORDER_COLOR);
		renderer.renderText(offsetX + PAGE_WIDTH + 3, offsetY - 1, 209,
				MENU_BORDER_COLOR);
		renderer.renderText(offsetX + PAGE_WIDTH + 4, offsetY - 1, 181,
				MENU_BORDER_COLOR);

		renderer.renderFill(offsetX - 2, offsetY, 1, PAGE_HEIGHT + 2, ' ',
				MENU_BORDER_COLOR);
		renderer.renderFill(offsetX + PAGE_WIDTH + 4, offsetY, 1,
				PAGE_HEIGHT + 2, ' ', MENU_BORDER_COLOR);
		renderer.renderFill(offsetX - 1, offsetY, 1, PAGE_HEIGHT + 2,
				(char) 179, MENU_BORDER_COLOR);
		renderer.renderFill(offsetX + PAGE_WIDTH + 3, offsetY, 1,
				PAGE_HEIGHT + 2, (char) 179, MENU_BORDER_COLOR);

		renderer.renderText(offsetX - 2, offsetY + PAGE_HEIGHT + 2, 198,
				MENU_BORDER_COLOR);
		renderer.renderText(offsetX - 1, offsetY + PAGE_HEIGHT + 2, 207,
				MENU_BORDER_COLOR);
		renderer.renderFill(offsetX, offsetY + PAGE_HEIGHT + 2, PAGE_WIDTH + 3,
				1, (char) 205, MENU_BORDER_COLOR);
		renderer.renderText(offsetX + PAGE_WIDTH + 3,
				offsetY + PAGE_HEIGHT + 2, 207, MENU_BORDER_COLOR);
		renderer.renderText(offsetX + PAGE_WIDTH + 4,
				offsetY + PAGE_HEIGHT + 2, 181, MENU_BORDER_COLOR);

		renderer.renderText(offsetX - 1, offsetY + 1, 198, MENU_BORDER_COLOR);
		renderer.renderFill(offsetX, offsetY + 1, PAGE_WIDTH + 3, 1,
				(char) 205, MENU_BORDER_COLOR);
		renderer.renderText(offsetX + PAGE_WIDTH + 3, offsetY + 1, 181,
				MENU_BORDER_COLOR);
	}

	/**
	 * @param t
	 * @return
	 */
	public static Menu renderThrowable(Throwable t) {
		t.printStackTrace();

		StringBuilder sb = new StringBuilder("$").append(t.toString());
		sb.append("\r\n");
		StackTraceElement e[] = t.getStackTrace();
		for (int i = 0; i < e.length; i++) {
			sb.append(e[i].toString()).append("\r\n");
		}

		return new Menu(ZGame.APP, sb.toString(), null);
	}

	public static void renderInputMessage(Renderer renderer, int y, String msg,
			boolean focused) {
		renderInputMessage(renderer, y, msg, focused, true);
	}
	
	/**
	 * Note that codes 0x01 and 0x02 are reserved coloring codes and cannot be
	 * displayed via this method.
	 */
	public static void renderInputMessage(Renderer renderer, int y, String msg,
			boolean focused, boolean useCodes) {
		// renderer.renderText(0, ZBoard.BOARD_HEIGHT - 2, msg, TEXT_COLOR);
		ElementColoring color = UI_TEXT_COLOR;
		int x = 0;
		for (int i = 0; i < msg.length(); i++) {
			char c = msg.charAt(i);
			if (useCodes && c == 2) {
				if (focused)
					color = UI_TEXT_COLOR;
			} else if (useCodes && c == 1) {
				if (focused)
					color = UI_SELECT_COLOR;
			} else if (c == '\n') {
				x = 0;
				y++;
			} else {
				renderer.renderText(x, y, c, color);
				x++;
			}
		}
	}

	/**
	 * @param renderer
	 * @param yoff
	 * @param currPageString
	 * @param focused
	 */
	public static void renderCenterMessage(Renderer renderer, int y,
			String msg, boolean focused) {
		ElementColoring color = UI_TEXT_COLOR;
		int x = centerText(msg, ZBoard.BOARD_WIDTH);
		for (int i = 0; i < msg.length(); i++) {
			char c = msg.charAt(i);
			if (c == 2) {
				if (focused)
					color = UI_TEXT_COLOR;
			} else if (c == 1) {
				if (focused)
					color = UI_SELECT_COLOR;
			} else if (c == '\n') {
				x = 0;
				y++;
			} else {
				renderer.renderText(x, y, c, color);
				x++;
			}
		}
	}

	/**
	 * @param m
	 * @return
	 */
	public static UIInteraction getFocusedInteraction(UIInteraction m) {
		if (m instanceof MultiInput) {
			return ((MultiInput) m).getFocusedInteraction();
		}

		return m;
	}
}
