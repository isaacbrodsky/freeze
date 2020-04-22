/**
 *
 */
package com.isaacbrodsky.freeze.graphics;

import com.isaacbrodsky.freeze.GameAppState;
import com.isaacbrodsky.freeze.elements.Invisible;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.game.GameState;
import com.isaacbrodsky.freeze.game.SuperZGameController;
import com.isaacbrodsky.freeze.game.ZGameController;
import com.isaacbrodsky.freeze.game.editor.EditorController;
import com.isaacbrodsky.freeze.game.editor.EditorMode;
import com.isaacbrodsky.freeze.utils.TimeAndMathUtils;

/**
 * @author isaac
 * 
 */
public final class Sidebar {
	public static final int SIDEBAR_OFFSET = Renderer.DISPLAY_WIDTH;
	public static final int SIDEBAR_WIDTH = 20;
	public static final int SIDEBAR_HEIGHT = Renderer.DISPLAY_HEIGHT;

	private static final ElementColoring SIDEBAR_COLOR = new ElementColoring(
			"YELLOW", "DARKBLUE");
	private static final ElementColoring ALT_SIDEBAR_COLOR = new ElementColoring(
			"WHITE", "DARKBLUE");
	private static final ElementColoring DISABLE_SIDEBAR_COLOR = new ElementColoring(
			"GRAY", "DARKBLUE");
	private static final ElementColoring BLUE_SIDEBAR_COLOR = new ElementColoring(
			"CYAN", "DARKBLUE");
	private static final ElementColoring TORCH_SIDEBAR_COLOR = new ElementColoring(
			"BROWN", "DARKBLUE");
	private static final ElementColoring TORCH_ALERT_SIDEBAR_COLOR = new ElementColoring(
			"BROWN", "DARKBLUE", true);
	private static final ElementColoring TORCH_LIT_SIDEBAR_COLOR = new ElementColoring(
			"RED", "DARKBLUE");
	private static final ElementColoring TORCH_LIT_BLINK_SIDEBAR_COLOR = new ElementColoring(
			"YELLOW", "DARKBLUE");

	private Sidebar() {
	}

	/**
	 * @param r
	 * @param gs
	 * @param game
	 */
	public static void renderSidebar(Renderer r, GameAppState gs,
			GameController game) {
		for (int x = 0; x < Sidebar.SIDEBAR_WIDTH; x++) {
			for (int y = 0; y < Sidebar.SIDEBAR_HEIGHT; y++) {
				r.set(x + Renderer.DISPLAY_WIDTH, y, new GraphicsBlock(
						new ElementColoring("DARKBLUE", "DARKBLUE"), 0));
			}
		}

		renderSelf(gs, r, game);
	}

	/**
	 * @param gs
	 * @param renderer
	 */
	private static void renderSelf(GameAppState gs, Renderer r,
			GameController game) {
		if (gs == GameAppState.PLAYING) {
			renderGame(r, game);
		} else if (gs == GameAppState.TITLE || gs == GameAppState.LOAD) {
			renderTitle(r, game);
		} else if (gs == GameAppState.EDITOR) {
			renderEditor(r, game);
		}
	}

	/**
	 * @param r
	 */
	private static void renderTitle(Renderer r, GameController game) {
		r.renderText(SIDEBAR_OFFSET + 2, 4, "P", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 3, 4, " to play", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 5, "W", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 3, 5, " for world list", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 6, "H", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 3, 6, " for hiscores", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 7, "R", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 3, 7, " for saved games", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 8, "E", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 3, 8, " for editor", SIDEBAR_COLOR);

		if (game != null) {
			r.renderText(SIDEBAR_OFFSET + 2, 2, "Game: ", SIDEBAR_COLOR);
			r.renderText(SIDEBAR_OFFSET + 8, 2, game.getState().gameName,
					ALT_SIDEBAR_COLOR);
		}
	}

	/**
	 * @param r
	 */
	private static void renderGame(Renderer r, GameController game) {
		int yOff = 6;
		int xOff = 4;
		GameState st = game.getState();
		if (game.getBoard().getState().dark != 0) {
			r.renderText(SIDEBAR_OFFSET + xOff, yOff - 2, "  [DARK]",
					SIDEBAR_COLOR);
		}
		if (game.getBoard().getState().timeLimit > 0) {
			r.renderText(SIDEBAR_OFFSET + 2, yOff - 1,
					"     Time:"
							+ (game.getBoard().getState().timeLimit - game
									.getState().timePassed), SIDEBAR_COLOR);
		}
		r.renderText(SIDEBAR_OFFSET + xOff, yOff, " Health:" + st.health,
				SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + xOff - 2, yOff, 2, ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + xOff, yOff + 1, "   Ammo:" + st.ammo,
				SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + xOff - 2, yOff + 1, 132,
				BLUE_SIDEBAR_COLOR);
		if (game instanceof ZGameController) {
			// torches only shown for ZZT, not SuperZZT
			String torchStr = "Torches:" + st.torches;
			r.renderText(SIDEBAR_OFFSET + xOff, yOff + 2, torchStr,
					SIDEBAR_COLOR);
			// torch is on
			if (st.tcycles > 0) {
				r
						.renderText(
								SIDEBAR_OFFSET + xOff + torchStr.length() + 1,
								yOff + 2,
								157,
								(TimeAndMathUtils.getOnes(st.tcycles) < 7) ? TORCH_LIT_SIDEBAR_COLOR
										: TORCH_LIT_BLINK_SIDEBAR_COLOR);
				String tcycles = Integer.toString((st.tcycles / 10) + 1);
				r.renderText(SIDEBAR_OFFSET + xOff + torchStr.length() + 2,
						yOff + 2, tcycles,
						(st.tcycles > 40) ? TORCH_SIDEBAR_COLOR
								: TORCH_ALERT_SIDEBAR_COLOR);
			}
			r.renderText(SIDEBAR_OFFSET + xOff - 2, yOff + 2, 157,
					TORCH_SIDEBAR_COLOR);
		} else if (game instanceof SuperZGameController) {
			String ztext = null;
			for (int i = 0; i < st.flags.length; i++) {
				if (st.flags[i] != null)
					if (st.flags[i].toLowerCase().startsWith("z")) {
						ztext = st.flags[i].substring(1);
						// select last
					}
			}
			if (ztext != null) {
				r.renderText(SIDEBAR_OFFSET + xOff - 1, yOff + 2, padTo(ztext,
						8)
						+ ":", SIDEBAR_COLOR);
				r.renderText(SIDEBAR_OFFSET + xOff - 2, yOff + 2, 'Z',
						ALT_SIDEBAR_COLOR);
			}
			if (st.stones >= 0)
				r.renderText(SIDEBAR_OFFSET + xOff + 8, yOff + 2, Integer
						.toString(st.stones), SIDEBAR_COLOR);
		}
		r.renderText(SIDEBAR_OFFSET + xOff, yOff + 3, "   Gems:" + st.gems,
				SIDEBAR_COLOR);
		r
				.renderText(SIDEBAR_OFFSET + xOff - 2, yOff + 3, 4,
						BLUE_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + xOff, yOff + 4, "  Score:" + st.score,
				SIDEBAR_COLOR);
		r
				.renderText(SIDEBAR_OFFSET + xOff, yOff + 5, "   Keys:",
						SIDEBAR_COLOR);
		r
				.renderText(SIDEBAR_OFFSET + xOff - 2, yOff + 5, 12,
						ALT_SIDEBAR_COLOR);
		for (int i = 0; i < st.keys.length; i++) {
			ElementColoring key = new ElementColoring(9 + i, ElementColoring
					.codeFromName("DARKBLUE"));
			if (st.keys[i] != 0)
				r.renderText(SIDEBAR_OFFSET + xOff + 8 + i, yOff + 5, 12, key);
		}

		if (game.getBoard().getState().shots == 0) {
			r.renderText(SIDEBAR_OFFSET + xOff, yOff + 6, " Can't shoot.",
					SIDEBAR_COLOR);
		} else if (game.getBoard().getState().shots != 255) {
			r.renderText(SIDEBAR_OFFSET + xOff, yOff + 6, "  Shots:"
					+ game.getBoard().getState().shots, SIDEBAR_COLOR);
		}

		if (game.getBoard().getState().restart != 0) {
			r.renderText(SIDEBAR_OFFSET + xOff, yOff + 7, " [REENTER]",
					SIDEBAR_COLOR);
		}
	}

	private static void renderEditor(Renderer r, GameController game) {
		EditorController editor = (EditorController) game;

		r.renderText(SIDEBAR_OFFSET + 2, 3, "TAB", ALT_SIDEBAR_COLOR);
		if (editor.getMode() == EditorMode.DRAW)
			r.renderText(SIDEBAR_OFFSET + 6, 3, "DRAW MODE", ALT_SIDEBAR_COLOR);
		else
			r.renderText(SIDEBAR_OFFSET + 6, 3, "draw mode", SIDEBAR_COLOR);

		r.renderText(SIDEBAR_OFFSET + 2, 4, "F1 F2", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 6, 5, "insert element", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 6, "F4", ALT_SIDEBAR_COLOR);
		if (editor.getMode() == EditorMode.TYPE)
			r.renderText(SIDEBAR_OFFSET + 6, 6, "TYPE MODE", ALT_SIDEBAR_COLOR);
		else
			r.renderText(SIDEBAR_OFFSET + 6, 6, "type mode", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 7, "X", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 6, 7, "fill", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 8, "B", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 6, 8, "board list", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 9, "I", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 6, 9, "inspector", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 10, "F", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 6, 10, "board info", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 11, "G", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 6, 11, "game info", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 12, "S", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 6, 12, "save", SIDEBAR_COLOR);

		r.renderText(SIDEBAR_OFFSET + 6, 14, editor.getCursor().toString(),
				SIDEBAR_COLOR);

		r.renderText(SIDEBAR_OFFSET + 2, 16, 'C', ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 3, 16, '/', SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 4, 16, 'V', ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 6, 16, "color:", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 13, 16, String.format("%02X", editor
				.getColorIdx()), ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 17, 'P', ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 6, 17, "palette", SIDEBAR_COLOR);
		for (int i = 0; i < editor.getBuffer().size(); i++) {
			int ch = editor.getBuffer().get(i).getDisplayCharacter();
			if (editor.getBuffer().get(i) instanceof Invisible)
				ch = 176;
			r.set(SIDEBAR_OFFSET + 6 + i, 18, new GraphicsBlock(editor
					.getBuffer().get(i).getColoring(), ch));
		}
		r.set(SIDEBAR_OFFSET + 6 + editor.getBufferIdx(), 19,
				new GraphicsBlock(
						(editor.getBufferIdx() > 4) ? ALT_SIDEBAR_COLOR
								: SIDEBAR_COLOR, 0x1E));
	}

	public static String padTo(String s, int len) {
		StringBuilder pad = new StringBuilder(s);
		while (pad.length() < len)
			pad.insert(0, ' ');
		return pad.toString();
	}
}
