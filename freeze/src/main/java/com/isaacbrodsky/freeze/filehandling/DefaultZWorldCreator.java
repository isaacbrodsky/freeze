/**
 *
 */
package com.isaacbrodsky.freeze.filehandling;

import java.io.IOException;
import java.util.ArrayList;

import com.isaacbrodsky.freeze.EmuMode;
import com.isaacbrodsky.freeze.elements.Element;
import com.isaacbrodsky.freeze.elements.ElementResolver;
import com.isaacbrodsky.freeze.elements.Empty;
import com.isaacbrodsky.freeze.elements.Normal;
import com.isaacbrodsky.freeze.elements.Player;
import com.isaacbrodsky.freeze.elements.data.Stats;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.game.GameState;
import com.isaacbrodsky.freeze.game.ZBoard;
import com.isaacbrodsky.freeze.game.ZGameController;

/**
 * @author isaac
 * 
 */
public class DefaultZWorldCreator implements Loader {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.filehandling.Loader#load(com.isaacbrodsky.freeze
	 * .filehandling.WorldList, java.lang.String)
	 */
	@Override
	public GameController load(WorldList base, String file) throws IOException {
		return createDefault();
	}

	public GameController createDefault() {
		GameState state = new GameState(1, 0, 0, new int[] { 0, 0, 0, 0, 0, 0,
				0 }, 100, 0, 0, 0, 0, 0, "NEW", new String[] { null, null,
				null, null, null, null, null, null, null, null }, 0, 0, -1);
		GameController game = new ZGameController(state);

		ArrayList<Board> b = new ArrayList<Board>(1);
		b.add(createDefaultBoard());
		game.setBoardList(b);

		return game;
	}

	public Board createDefaultBoard() {
		Element[][][] elements = createDefaultElementSet();

		Board board = new ZBoard(elements, "Untitled Board", 255, 0, 0, 0, 0,
				0, 0, "", 3, 3, 0, 3, 3);

		return board;
	}

	/**
	 * @return
	 */
	public Element[][][] createDefaultElementSet() {
		Element[][][] elements = new Element[ZBoard.BOARD_WIDTH][ZBoard.BOARD_HEIGHT][ZBoard.BOARD_DEPTH];

		ElementResolver resolve = new ElementResolver(EmuMode.ZZT);
		
		for (int x = 0; x < ZBoard.BOARD_WIDTH; x++) {
			for (int y = 0; y < ZBoard.BOARD_HEIGHT; y++) {
				if (x == 0 || x == ZBoard.BOARD_WIDTH - 1 || y == 0
						|| y == ZBoard.BOARD_HEIGHT - 1) {
					elements[x][y][0] = resolve.resolve(Normal.class,
							0x07, x, y);
					elements[x][y][1] = null;
				} else if (x == 4 && y == 4) {
					elements[x][y][0] = resolve.resolve(Player.class,
							0x1F, x, y);
					Stats playerStats = new Stats(4, 4, 0, 0, 0, 0, 0, 1, -1,
							"");
					elements[x][y][0].setStats(playerStats);
					elements[x][y][1] = resolve.resolve(Empty.class, 0,
							x, y);
				} else {
					elements[x][y][0] = resolve.resolve(Empty.class, 0,
							x, y);
					elements[x][y][1] = null;
				}
			}
		}

		return elements;
	}
}
