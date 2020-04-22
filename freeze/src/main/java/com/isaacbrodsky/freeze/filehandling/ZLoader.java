/**
 * 
 */
package com.isaacbrodsky.freeze.filehandling;

import com.isaacbrodsky.freeze.elements.Element;
import com.isaacbrodsky.freeze.elements.ElementResolver;
import com.isaacbrodsky.freeze.elements.data.Stats;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.game.GameState;
import com.isaacbrodsky.freeze.game.ZBoard;
import com.isaacbrodsky.freeze.game.ZGameController;
import com.isaacbrodsky.freeze.utils.StringBackedInputStream;
import com.isaacbrodsky.freeze.utils.UnsignedDataInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author isaac
 * 
 */
public class ZLoader implements Loader {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.filehandling.Loader#load(com.isaacbrodsky.freeze
	 * .filehandling.WorldList, java.lang.String)
	 */
	@Override
	public GameController load(WorldList base, String file) throws IOException {
		return load(base.getWorldBytes(file));
	}

	public static GameController load(InputStream raw) throws IOException {
		UnsignedDataInputStream input = new UnsignedDataInputStream(raw);

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
		GameController game = new ZGameController(state);

		ArrayList<Board> boardsList = new ArrayList<Board>(boards);
		for (int i = 0; i <= boards; i++) {
			int boardLength = input.readSignedLEShort();
			byte[] b = new byte[boardLength];
			input.read(b, 0, boardLength);
			try {
				// System.out.println("**************** BOARD " + i + " " +
				// boardLength);
				boardsList.add(loadBoard(game, b));
			} catch (Exception e) {
				e.printStackTrace();
				boardsList.add(null);
			}
		}

		input.close();
		game.setBoardList(boardsList);

		return game;
	}

	/**
	 * @param game
	 * @param b
	 * @return
	 * @throws IOException
	 */
	public static Board loadBoard(GameController game, byte[] b) throws IOException {
		UnsignedDataInputStream input = new UnsignedDataInputStream(
				new StringBackedInputStream(b));
		String boardName = input.readZZTString(33);
		input.skip(17);

		Element[][][] elements = loadRLEBoard(game.getElementResolver(), input);

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
		int playerX = -1;
		int playerY = -1;

		// stat elements
		int numStats = input.readSignedLEShort();

		ArrayList<Stats> delayedStats = new ArrayList<Stats>();
		ArrayList<Stats> statsList = new ArrayList<Stats>();
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
			if (underT == 1) {
				// XXX DEBUG
				System.out.println("Stat #" + (statsList.size() + 1)
						+ " Stat claims to have a board edge under it.");
			}

			input.skip(4);// unk? ("pointer")

			int currInstr = input.readSignedLEShort();
			int oopLength = input.readSignedLEShort();
			input.skip(8);

			String oop = "";
			if (oopLength > 0) {
				oop = input.readZZTStringFixedLength(oopLength);
			}

			if (statX == 0 || statY == 0) {
				System.out.println("Stat #" + (statsList.size() + 1)
						+ " Disregarding stat: " + statX + " " + statY);
				continue;
			}

			statX = (statX != 0) ? statX - 1 : 0;
			statY = (statY != 0) ? statY - 1 : 0;

			if (statY > ZBoard.BOARD_HEIGHT || statX > ZBoard.BOARD_WIDTH) {
				System.err.println("Stat #" + (statsList.size() + 1)
						+ " Out of bounds stat: " + statX + "/" + statY);
				continue;
			}

			Stats s = new Stats(statX, statY, p1, p2, p3, stepX, stepY, cycle,
					follower, leader, currInstr, oopLength, oop);
			statsList.add(s);
			if (oopLength < 0 || leader > 0) {
				delayedStats.add(s);
			} else {
				if (elements[statX][statY][0] != null) {
					elements[statX][statY][0].setStats(s);
				}
			}
			elements[statX][statY][1] = game.getElementResolver().resolve(
					underT, underC, statX, statY);

			if (j == 0) {
				playerX = statX;
				playerY = statY;
			}
		}

		mergeStatsList(elements, delayedStats, statsList);

		Board board = new ZBoard(elements, boardName, shots, dark, boardNorth,
				boardSouth, boardWest, boardEast, restart, message, enterX,
				enterY, timeLimit, playerX, playerY);

		return board;
	}

	/**
	 * @param elements
	 * @param delayedStats
	 * @param statsList
	 */
	private static void mergeStatsList(Element[][][] elements,
			ArrayList<Stats> delayedStats, ArrayList<Stats> statsList) {
		// Delayed stats are ones which depend on other stat elements
		// Bound objects and centipedes are the main users here.
		for (Stats s : delayedStats) {
			int statX = s.getX();
			int statY = s.getY();

			if (s.getOopLength() != 0) {
				String oop = null;
				int oopOffset = Math.abs(s.getOopLength());// Do not adjust to
				// zero
				// based or anything like that.
				// Probably because the ACE is always 0.
				if (statsList.size() > oopOffset) {
					oop = statsList.get(oopOffset).getOop();
					s = new Stats(s.getX(), s.getY(), s.getP1(), s.getP2(), s
							.getP3(), s.getStepX(), s.getStepY(), s.getCycle(),
							s.getFollower(), s.getLeader(), s.getCurrInstr(), s
									.getOopLength(), oop);
				}
			} else {
				int l = Math.abs(s.getLeader());
				int f = Math.abs(s.getFollower());
				if (statsList.size() > f && statsList.size() > l) {
					Element fe = elements[statsList.get(f).getX()][statsList
							.get(f).getY()][0];
					Element le = elements[statsList.get(l).getX()][statsList
							.get(l).getY()][0];
					s = new Stats(s.getX(), s.getY(), s.getP1(), s.getP2(), s
							.getP3(), s.getStepX(), s.getStepY(), s.getCycle(),
							-1, fe, le, null);
				}
			}
			if (elements[statX][statY][0] != null) {
				elements[statX][statY][0].setStats(s);
			}
		}
	}

	/**
	 * @param input
	 * @return
	 * @throws IOException
	 */
	private static Element[][][] loadRLEBoard(ElementResolver resolve,
			UnsignedDataInputStream input) throws IOException {
		Element[][][] elements = new Element[ZBoard.BOARD_WIDTH][ZBoard.BOARD_HEIGHT][ZBoard.BOARD_DEPTH];
		int x = 0, y = 0;
		for (int pos = 0; pos < ZBoard.BOARD_WIDTH * ZBoard.BOARD_HEIGHT;) {
			int reps = input.read();
			int type = input.read();
			int col = input.read();

			for (int i = 0; i < reps; i++) {
				if (pos == ZBoard.BOARD_WIDTH * ZBoard.BOARD_HEIGHT) {
					System.err.println("pos at limit");
					break;
				}
				elements[x][y][0] = resolve.resolve(type, col, x, y);
				if (elements[x][y][0] == null)
					System.err.println("Unresolved element @ " + x + "x" + y
							+ ": " + type + " $" + col);
				elements[x][y][1] = null;
				x++;
				if (x == ZBoard.BOARD_WIDTH) {
					y++;
					x = 0;
				}
				pos++;
			}
		}
		return elements;
	}
}
