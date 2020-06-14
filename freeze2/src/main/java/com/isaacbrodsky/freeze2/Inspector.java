/**
 * 
 */
package com.isaacbrodsky.freeze2;

import com.isaacbrodsky.freeze2.elements.Element;
import com.isaacbrodsky.freeze2.game.*;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.menus.Menu;
import com.isaacbrodsky.freeze2.menus.UIInteraction;

import java.util.List;
import java.util.stream.Collectors;

public final class Inspector {

	private Inspector() {

	}

	/**
	 * Constructs a menu detailing the object at the given location on the given
	 * board.
	 * 
	 * @param board
	 * @param x
	 * @param y
	 */
	public static Menu inspectElement(GameController game, Board board, int x, int y) {
		Tile t = board.tileAt(x, y);
		Element element = game.resolveElement(t.getType());

		StringBuilder msg = new StringBuilder("$").append(element);
		msg.append("\r\nType ID: ")
				.append(t.getType());
		msg.append(" (Color: ").append(t.getColor())
				.append(")");
		msg.append("\r\nColor: ").append(ElementColoring.forCode(t.getColor()));
		msg.append("\r\nImplementation: ").append(element.impl().getClass().getSimpleName());
		msg.append("\r\nX/Y: ").append(x).append("/").append(y);

		long numStats = board.getStats().stream()
				.filter(s -> s.x == x && s.y == y)
				.count();

		msg.append("\r\nStats here: ").append(numStats);

		for (int i = 0; i < board.getStats().size(); i++) {
			Stat s = board.getStats().get(i);
			if (s.x != x || s.y != y) {
				continue;
			}
			msg.append("\r\n\r\n");
			msg.append("\r\n$Stat ").append(i);
			msg.append("\r\n").append(s.toString());
		}

		msg.append("\r\n\r\n");
		msg.append("$Element definition");
		msg.append("\r\n");
		msg.append(element.def().toString());

		return new Menu("Element inspector: "
				+ element.name(), msg.toString(),
				null, Menu.SendMode.NO, true);
	}

	/**
	 * Creates a menu detailing the given board.
	 * 
	 * @param board
	 * @param id
	 * @return
	 */
	public static UIInteraction inspectBoard(Board board, int id) {
		BoardState s = board.getState();
		StringBuilder msg = new StringBuilder("$").append(board.getClass()
				.getSimpleName());
		msg.append("\r\nIndex: ").append(id);
		msg.append("\r\nDimensions: ").append(board.getWidth()).append("x")
				.append(board.getHeight());
		msg.append("\r\n").append(s.toString().replaceAll("\t", "    "));

		return new Menu("Board inspector: " + board.getState().boardName, msg
				.toString(), null, Menu.SendMode.NO, true);
	}

	/**
	 * Creates a menu detailing the given game.
	 * 
	 * @param game
	 * @return
	 */
	public static UIInteraction inspectGame(GameController game) {
		GameState s = game.getState();
		StringBuilder msg = new StringBuilder("$").append(game.getClass()
				.getSimpleName());
		msg.append("\r\n").append("Engine: ").append(game.getEmuMode());
		msg.append("\r\n").append(s.toString().replaceAll("\t", "    "));

		return new Menu("Game inspector: " + s.gameName, msg.toString(), null,
				Menu.SendMode.NO, true);
	}
}
