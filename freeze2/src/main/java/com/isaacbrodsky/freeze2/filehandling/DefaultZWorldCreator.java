/**
 *
 */
package com.isaacbrodsky.freeze2.filehandling;

import com.isaacbrodsky.freeze2.elements.ZElement;
import com.isaacbrodsky.freeze2.game.*;
import com.isaacbrodsky.freeze2.game.editor.AddElementUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author isaac
 * 
 */
public class DefaultZWorldCreator implements Loader {
	@Override
	public GameController load(WorldList base, String file) throws IOException {
		return createDefault();
	}

	public GameController createDefault() {
		GameState state = new GameState(1, 0, 0, new int[] { 0, 0, 0, 0, 0, 0,
				0 }, 100, 0, 0, 0, 0, 0, "NEW",
				new String[10], 0, 0, -1);
		ArrayList<Board> boards = new ArrayList<>(1);
		boards.add(createDefaultBoard());
		GameController game = new ZGameController(state, boards);

		return game;
	}

	public Board createDefaultBoard() {
		Tile[][] tiles = new Tile[ZBoard.BOARD_WIDTH][ZBoard.BOARD_HEIGHT];
		List<Stat> stats = new ArrayList<>();

		for (int x = 0; x < ZBoard.BOARD_WIDTH; x++) {
			for (int y = 0; y < ZBoard.BOARD_HEIGHT; y++) {
				if (x == 0 || x == ZBoard.BOARD_WIDTH - 1 || y == 0
						|| y == ZBoard.BOARD_HEIGHT - 1) {
					tiles[x][y] = new Tile(ZElement.NORMAL, 0x07);
				} else if (x == 4 && y == 4) {
					tiles[x][y] = new Tile(ZElement.PLAYER, 0x1F);
					Stat playerStats = AddElementUtils.createDefaultStat(ZElement.PLAYER.def(), x + 1, y + 1);
					stats.add(playerStats);
				} else {
					tiles[x][y] = new Tile(ZElement.EMPTY, 0);
				}
			}
		}

		BoardState state = new BoardState("Untitled Board", 255, 0, 0, 0, 0,
				0, 0, "", 3, 3, 0);

		Board board = new ZBoard(state, tiles, stats);

		return board;
	}
}
