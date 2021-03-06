/**
 * 
 */
package com.isaacbrodsky.freeze2.filehandling;

import com.isaacbrodsky.freeze2.game.*;
import com.isaacbrodsky.freeze2.utils.StringBackedInputStream;
import com.isaacbrodsky.freeze2.utils.UnsignedDataInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author isaac
 * 
 */
public class ZLoader implements Loader {
	@Override
	public GameController load(WorldList base, String file) throws IOException {
		return load(base.getWorldBytes(file));
	}

	public static GameController load(InputStream raw) throws IOException {
		try (UnsignedDataInputStream input = new UnsignedDataInputStream(raw)) {

			int magic1, magic2;
			magic1 = input.read();
			magic2 = input.read();
			if (magic1 != 255 || magic2 != 255)
				System.out.println("Magic BAD!");

			int boards = input.readSignedLEShort();
			int ammo = input.readSignedLEShort();
			int gems = input.readSignedLEShort();
			int[] keys = new int[7];
			for (int i = 0; i < 7; i++) {
				keys[i] = input.read();
			}
			int health = input.readSignedLEShort();
			int startBoard = input.readSignedLEShort();
			int torches = input.readSignedLEShort();
			int tcycles = input.readSignedLEShort();
			int ecycles = input.readSignedLEShort();

			input.skip(2); // unk or unused

			int score = input.readSignedLEShort();

			String gameName = input.readZZTString(20);

			String[] flags = new String[10];
			for (int i = 0; i < 10; i++) {
				flags[i] = input.readZZTString(20);
				if (flags[i].trim().equals(""))
					flags[i] = null;
			}

			int timePassed = input.readSignedLEShort();
			input.skip(2); // Apparently: "This stores a runtime memory location
			// that points to the status element currently being used by the
			// player. Its value is not used when loading worlds,
			// and is always set to 0 when saving worlds."
			int isSave = input.read();
			input.skip(7 + (15 * 16));

			GameState state = new GameState(boards, ammo, gems, keys, health,
					startBoard, torches, tcycles, ecycles, score, gameName, flags,
					timePassed, isSave, -1);

			ArrayList<Board> boardsList = new ArrayList<Board>(boards);
			for (int i = 0; i <= boards; i++) {
				int boardLength = input.readSignedLEShort();
				byte[] b = new byte[boardLength];
				// TODO: Do something if wrong number of bytes read
				input.read(b, 0, boardLength);
				try {
					// System.out.println("**************** BOARD " + i + " " +
					// boardLength);
					boardsList.add(loadBoard(b));
				} catch (Exception e) {
					System.err.println(String.format("Corrupt board %d/%d", i, boards));
					boardsList.add(null);
				}
			}

			GameController game = new ZGameController(state, boardsList);

			return game;
		}
	}

	public static Board loadBoard(byte[] b) throws IOException {
		UnsignedDataInputStream input = new UnsignedDataInputStream(
				new StringBackedInputStream(b));
		String boardName = input.readZZTString(33);
		input.skip(17);

		Tile[][] tiles = loadRLEBoard(input);

		int shots = input.read();
		int dark = input.read();
		int boardNorth = input.read();
		int boardSouth = input.read();
		int boardWest = input.read();
		int boardEast = input.read();
		int restart = input.read();
		String message = input.readZZTString(58);
		int enterX = input.read();
		int enterY = input.read();
		int timeLimit = input.readSignedLEShort();
		input.skip(16);

		// stat elements
		int numStats = input.readSignedLEShort();

		ArrayList<Stat> statsList = new ArrayList<>();
		for (int j = 0; j < numStats + 1; j++) {
			int statX = input.read();
			int statY = input.read();
			int stepX = input.readSignedLEShort();
			int stepY = input.readSignedLEShort();
			int cycle = input.readSignedLEShort();
			int p1 = input.read();
			int p2 = input.read();
			int p3 = input.read();

			int follower = input.readSignedLEShort();
			int leader = input.readSignedLEShort();

			int underT = input.read();
			int underC = input.read();
			// Explanation: This is the message timer for the board.
			// However, the message timer tile is not saved, so this
			// stat is unused.
//			if (underT == 1) {
//				System.out.println("Stat #" + (statsList.size() + 1)
//						+ " Stat claims to have a board edge under it.");
//			}
//			if (statX == 0 || statY == 0) {
//				System.out.println("Stat #" + (statsList.size() + 1)
//						+ " is at " + statX + " " + statY);
//			}
			Tile under = new Tile(underT, underC);

			input.skip(4);// unk? ("pointer")

			int currInstr = input.readSignedLEShort();
			int oopLength = input.readSignedLEShort();
			input.skip(8);

			String oop = "";
			if (oopLength > 0) {
				oop = input.readZZTStringFixedLength(oopLength);
			}

			if (statY > ZBoard.BOARD_HEIGHT || statX > ZBoard.BOARD_WIDTH) {
				System.err.println("Stat #" + (statsList.size() + 1)
						+ " Out of bounds stat: " + statX + "/" + statY);
			}

			Stat s = new Stat(statX, statY, p1, p2, p3, stepX, stepY, cycle,
					follower, leader, currInstr, under, oopLength, oop);
			statsList.add(s);
		}

		BoardState state = new BoardState(boardName, shots, dark, boardNorth,
				boardSouth, boardWest, boardEast, restart, message, enterX,
				enterY, timeLimit);
		Board board = new ZBoard(state, tiles, statsList);

		return board;
	}

	/**
	 * @param input
	 * @return
	 * @throws IOException
	 */
	private static Tile[][] loadRLEBoard(UnsignedDataInputStream input) throws IOException {
		Tile[][] tiles = new Tile[ZBoard.BOARD_WIDTH][ZBoard.BOARD_HEIGHT];
		int x = 0, y = 0;
		for (int pos = 0; pos < ZBoard.BOARD_WIDTH * ZBoard.BOARD_HEIGHT;) {
			int reps = input.read();
			if (reps == 0) {
				// https://github.com/cknave/kevedit/issues/5
				reps = 256;
			}
			int type = input.read();
			int col = input.read();

			for (int i = 0; i < reps; i++) {
				if (pos == ZBoard.BOARD_WIDTH * ZBoard.BOARD_HEIGHT) {
					System.err.println("pos at limit (board RLE possibly corrupt)");
					break;
				}
				tiles[x][y] = new Tile(type, col);
				x++;
				if (x == ZBoard.BOARD_WIDTH) {
					y++;
					x = 0;
				}
				pos++;
			}
		}
		return tiles;
	}
}
