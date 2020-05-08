/**
 * 
 */
package com.isaacbrodsky.freeze2.menus.stateditor;

import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.game.Stat;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.game.editor.EditorController;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.Renderer;
import com.isaacbrodsky.freeze2.menus.MenuCallback;
import com.isaacbrodsky.freeze2.menus.MenuUtils;
import com.isaacbrodsky.freeze2.menus.UIInteraction;

import java.awt.event.KeyEvent;

/**
 * @author isaac
 * 
 */
public class StatInput extends MenuUtils implements UIInteraction {
	private final EditorController editor;
	private final Board board;
	public int statIndex;
	public Stat stat;
	private UIInteraction statEditor;

	public StatInput(EditorController editor, Board board, int statIndex, Stat stat) {
		this.editor = editor;
		this.board = board;
		this.statIndex = statIndex;
		this.stat = stat;
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
		if (statEditor != null)
			return statEditor.keyPress(key);
		else if (key == KeyEvent.VK_RIGHT) {
			Stat defaultStat = stat;
			if (defaultStat == null) {
				defaultStat = new Stat(editor.getCursor().getX(), editor.getCursor().getY());
			}
			statEditor = StatEditor.makeStatInspector(editor, getPointedTile(board, stat), new Stat(defaultStat), new MenuCallback() {
				@Override
				public void menuCommand(String cmd, Object rider) {
					if (cmd.equals("SUBMIT")) {
						stat = new Stat((Stat) rider);
					}
				}
			});
			return true;
		} else if (key == KeyEvent.VK_DELETE) {
			stat = null;
			return true;
		}
		return false;
	}

	@Override
	public void render(Renderer renderer, int yoff, boolean focused) {
		String selectText = "";
		if (focused) {
			selectText = " \001RIGHT\002 to edit";
		}

		final String title = makeStatTitle(editor, board, stat, statIndex);

		renderInputMessage(renderer, yoff, title + selectText, focused);

		if (statEditor != null)
			statEditor.render(renderer, 0, focused);
	}

	@Override
	public boolean stillAlive() {
		return true;
	}

	@Override
	public void tick() {
		if (statEditor != null) {
			statEditor.tick();
			if (!statEditor.stillAlive())
				statEditor = null;
		}
	}

	@Override
	public UIInteraction getFocusedInteraction() {
		if (statEditor != null)
			return statEditor.getFocusedInteraction();
		return this;
	}

	private static Tile getPointedTile(Board board, Stat stat) {
		if (stat != null && board.inBounds(stat.x, stat.y))
			return board.tileAt(stat.x, stat.y);
		else
			return null;
	}

	public static String makeStatTitle(GameController game, Board board, Stat stat, int statIndex) {
		if (stat == null) {
			return String.format("%d: (none)", statIndex);
		} else {
			Tile tile = getPointedTile(board, stat);
			String tileText = tile == null ? "OFFSCREEN" :
					game.resolveElement(tile.getType()).name() + " " + ElementColoring.forCode(tile.getColor());
			return String.format("%d: %s @ (%d, %d)", statIndex, tileText, stat.x, stat.y);
		}
	}
}
