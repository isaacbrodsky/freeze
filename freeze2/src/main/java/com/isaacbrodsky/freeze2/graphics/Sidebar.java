/**
 *
 */
package com.isaacbrodsky.freeze2.graphics;

import com.isaacbrodsky.freeze2.GameAppState;
import com.isaacbrodsky.freeze2.elements.Element;
import com.isaacbrodsky.freeze2.game.EmptyBoard;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.game.GameState;
import com.isaacbrodsky.freeze2.game.ZGameController;
import com.isaacbrodsky.freeze2.game.editor.EditorController;
import com.isaacbrodsky.freeze2.game.editor.EditMode;
import com.isaacbrodsky.freeze2.game.editor.ElementBuffer;
import com.isaacbrodsky.freeze2.utils.TimeAndMathUtils;

/**
 * @author isaac
 * 
 */
public final class Sidebar {
	public static final int SIDEBAR_OFFSET = Renderer.DISPLAY_WIDTH;
	public static final int SIDEBAR_WIDTH = 20;
	public static final int SIDEBAR_HEIGHT = Renderer.DISPLAY_HEIGHT;

	private static final ElementColoring SIDEBAR_COLOR = ElementColoring.forNames(
			NamedColor.YELLOW, NamedColor.DARKBLUE);
	private static final ElementColoring ALT_SIDEBAR_COLOR = ElementColoring.forNames(
			NamedColor.WHITE, NamedColor.DARKBLUE);
	private static final ElementColoring WARN_SIDEBAR_COLOR = ElementColoring.forNames(
			NamedColor.RED, NamedColor.DARKBLUE, true);
	private static final ElementColoring DISABLE_SIDEBAR_COLOR = ElementColoring.forNames(
			NamedColor.GRAY, NamedColor.DARKBLUE);
	private static final ElementColoring BLUE_SIDEBAR_COLOR = ElementColoring.forNames(
			NamedColor.CYAN, NamedColor.DARKBLUE);
	private static final ElementColoring TORCH_SIDEBAR_COLOR = ElementColoring.forNames(
			NamedColor.BROWN, NamedColor.DARKBLUE);
	private static final ElementColoring TORCH_ALERT_SIDEBAR_COLOR = ElementColoring.forNames(
			NamedColor.BROWN, NamedColor.DARKBLUE, true);
	private static final ElementColoring TORCH_LIT_SIDEBAR_COLOR = ElementColoring.forNames(
			NamedColor.RED, NamedColor.DARKBLUE);
	private static final ElementColoring TORCH_LIT_BLINK_SIDEBAR_COLOR = ElementColoring.forNames(
			NamedColor.YELLOW, NamedColor.DARKBLUE);
	private static final ElementColoring BLANK_SIDEBAR_COLOR = ElementColoring.forNames(
			NamedColor.WHITE, NamedColor.BLACK);

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
						ElementColoring.forNames(NamedColor.DARKBLUE, NamedColor.DARKBLUE), 0));
			}
		}

		renderSelf(gs, r, game);
	}

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

	private static void renderTitle(Renderer r, GameController game) {
		r.renderText(SIDEBAR_OFFSET + 2, 4, "Q", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 3, 4, " to exit", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 5, "P", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 3, 5, " to play", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 6, "W", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 3, 6, " for world list", SIDEBAR_COLOR);
//		r.renderText(SIDEBAR_OFFSET + 2, 7, "H", ALT_SIDEBAR_COLOR);
//		r.renderText(SIDEBAR_OFFSET + 3, 7, " for hiscores", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 8, "R", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 3, 8, " for saved games", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 9, "E", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 3, 9, " for editor", SIDEBAR_COLOR);

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
		}
//		else if (game instanceof SuperZGameController) {
//			String ztext = null;
//			for (int i = 0; i < st.flags.length; i++) {
//				if (st.flags[i] != null)
//					if (st.flags[i].toLowerCase().startsWith("z")) {
//						ztext = st.flags[i].substring(1);
//						// select last
//					}
//			}
//			if (ztext != null) {
//				r.renderText(SIDEBAR_OFFSET + xOff - 1, yOff + 2, padTo(ztext,
//						8)
//						+ ":", SIDEBAR_COLOR);
//				r.renderText(SIDEBAR_OFFSET + xOff - 2, yOff + 2, 'Z',
//						ALT_SIDEBAR_COLOR);
//			}
//			if (st.stones >= 0)
//				r.renderText(SIDEBAR_OFFSET + xOff + 8, yOff + 2, Integer
//						.toString(st.stones), SIDEBAR_COLOR);
//		}
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
			ElementColoring key = ElementColoring.forCode((NamedColor.DARKBLUE.ordinal() << 4) | (9 + i));
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

		r.renderText(SIDEBAR_OFFSET + 2, 3, "H", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 6, 3, "Help", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 4, "ESC", ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 6, 4, "Quit", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 6, "Edit: ", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 8, 6, editor.getEditMode().name(), ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 7, "View: ", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 8, 7, editor.getViewMode().name(), ALT_SIDEBAR_COLOR);

		r.renderText(SIDEBAR_OFFSET + 6, 14, editor.getCursor().toString(),
				SIDEBAR_COLOR);

		r.renderText(SIDEBAR_OFFSET + 2, 16, 'C', ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 3, 16, '/', SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 4, 16, 'V', ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 6, 16, "Color:", SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 13, 16, String.format("%02X", editor
				.getColorIdx()), ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 16, 16, 2, ElementColoring.forCode(editor.getColorIdx()));
		r.renderText(SIDEBAR_OFFSET + 17, 16, editor.getColorIdx(), BLANK_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 2, 17, "A", ALT_SIDEBAR_COLOR);
		if (editor.isBufferLocked())
			r.renderText(SIDEBAR_OFFSET + 4, 17, 12, ALT_SIDEBAR_COLOR);
		r.renderText(SIDEBAR_OFFSET + 6, 17, "Palette", SIDEBAR_COLOR);
		for (int i = 0; i < editor.getBuffer().size(); i++) {
			ElementBuffer buf = editor.getBuffer().get(i);
			Element e = game.resolveElement(buf.tile.getType());
			GraphicsBlock block = buf.graphics
					.orElseGet(() -> game.resolveElement(buf.tile.getType()).impl().draw(game, EmptyBoard.INSTANCE, 0, 0, buf.tile, e));
			r.set(SIDEBAR_OFFSET + 2 + i, 18,
					editor.overrideGraphicsBlock(0, 0, block, buf, true));
		}
		r.set(SIDEBAR_OFFSET + 2 + editor.getBufferIdx(), 19,
				new GraphicsBlock(
						(editor.getBufferIdx() < EditorController.USER_BUFFER_SLOTS) ? ALT_SIDEBAR_COLOR
								: SIDEBAR_COLOR, 0x1E));
		if (editor.getBoard() != null) {
			long offscreenStatCount = game.getBoard().getStats().stream()
					.filter(s -> s.x < 1 || s.x > game.getBoard().getWidth()
							|| s.y < 1 || s.y > game.getBoard().getHeight())
					.count();
			r.renderText(SIDEBAR_OFFSET + 2, 21, "Stats:     ", SIDEBAR_COLOR);
			r.renderText(SIDEBAR_OFFSET + 2 + 11, 21, Integer.toString(game.getBoard().getStats().size()), ALT_SIDEBAR_COLOR);
			r.renderText(SIDEBAR_OFFSET + 2, 22, "Offscreen: ", SIDEBAR_COLOR);
			r.renderText(SIDEBAR_OFFSET + 2 + 11, 22, Long.toString(offscreenStatCount), ALT_SIDEBAR_COLOR);
		}
		if (editor.getBoard() == null || editor.isAbnormal()) {
			r.renderText(SIDEBAR_OFFSET + 2 + 2, 23, "-ABNORMAL-", WARN_SIDEBAR_COLOR);
		} else if (editor.isOverLimit()) {
			// TODO: Warning is only for this board
			r.renderText(SIDEBAR_OFFSET + 2 + 2, 23, "OVER LIMIT", WARN_SIDEBAR_COLOR);
		}
	}

	public static String padTo(String s, int len) {
		StringBuilder pad = new StringBuilder(s);
		while (pad.length() < len)
			pad.insert(0, ' ');
		return pad.toString();
	}
}
