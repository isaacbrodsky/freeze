/**
 * 
 */
package com.isaacbrodsky.freeze2.menus;

import com.isaacbrodsky.freeze2.ZGame;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.NamedColor;
import com.isaacbrodsky.freeze2.graphics.Renderer;

/**
 * @author isaac
 * 
 */
public class MenuUtils {
	public static final ElementColoring MENU_POSARROW_COLOR = ElementColoring.forNames(
			NamedColor.RED, NamedColor.DARKBLUE);
	public static final ElementColoring MENU_ALT_COLOR = ElementColoring.forNames(
			NamedColor.WHITE, NamedColor.DARKBLUE);
	public static final ElementColoring MENU_TEXT_COLOR = ElementColoring.forNames(
			NamedColor.YELLOW, NamedColor.DARKBLUE);
	public static final ElementColoring MENU_BORDER_COLOR = ElementColoring.forNames(
			NamedColor.WHITE, NamedColor.BLACK);

	public static final ElementColoring UI_TEXT_COLOR = ElementColoring.forNames(
			NamedColor.WHITE, NamedColor.BLACK);
	public static final ElementColoring UI_SELECT_COLOR = ElementColoring.forNames(
			NamedColor.BLACK, NamedColor.GRAY);
	public static final ElementColoring UI_BLANK_COLOR = ElementColoring.forNames(
			NamedColor.BLACK, NamedColor.BLACK);

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

	public static Menu renderThrowable(Throwable t) {
		// TODO: Don't use Menu for this because lines tend to exceed the line length
		t.printStackTrace();

		StringBuilder sb = new StringBuilder("$").append(t.toString());
		sb.append("\r\n");
		StackTraceElement e[] = t.getStackTrace();
		for (int i = 0; i < e.length; i++) {
			sb.append(e[i].toString()).append("\r\n");
		}

		String wrapped = wrapText(sb);
		return new Menu(ZGame.APP_SHORT, wrapped, null);
	}

	public static String wrapText(String str) {
		return wrapText(new StringBuilder(str));
	}

	/**
	 * Very simple text wrapping
	 */
	public static String wrapText(StringBuilder sb) {
		int lineLength = 0;
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == '\r' || sb.charAt(i) == '\n') {
				lineLength = 0;
			} else {
				lineLength++;
				if (lineLength == PAGE_WIDTH) {
					lineLength = 0;
					sb.insert(i, "\r\n");
				}
			}
		}
		return sb.toString();
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
	 * @param focused
	 */
	public static void renderCenterMessage(Renderer renderer, int y,
			String msg, boolean focused) {
		ElementColoring color = UI_TEXT_COLOR;
		int x = centerText(msg, Renderer.DISPLAY_WIDTH);
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

	public static UIInteraction getTypingTarget(UIInteraction m) {
		// TODO this is a hack that supports only MultiInput receiving typing
		if (m instanceof MultiInput) {
			return m.getFocusedInteraction();
		}

		return m;
	}
}
