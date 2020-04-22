/**
 * 
 */
package com.isaacbrodsky.freeze.filehandling;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.isaacbrodsky.freeze.elements.Element;
import com.isaacbrodsky.freeze.elements.ElementResolver;
import com.isaacbrodsky.freeze.elements.data.Stats;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.game.GameState;
import com.isaacbrodsky.freeze.game.SuperZBoard;
import com.isaacbrodsky.freeze.game.SuperZGameController;
import com.isaacbrodsky.freeze.utils.StringBackedInputStream;
import com.isaacbrodsky.freeze.utils.UnsignedDataInputStream;

/**
 * @author isaac
 * 
 */
public class SuperZLoader implements Loader {

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
		if (magic1 != 254 || magic2 != 255)
			System.out.println("SuperMagic BAD!");

		int boards = input.readSignedLEShort();
		int ammo = input.readSignedLEShort();
		int gems = input.readSignedLEShort();
		int[] keys = new int[7];
		for (int i = 0; i < 7; i++) {
			keys[i] = input.read();
		}
		int health = input.readSignedLEShort();
		int startBoard = input.readSignedLEShort();
//		int torches = input.readSignedLEShort();
		int torches = 0;
		input.skip(2);
		int score = input.readSignedLEShort();
//		int tcycles = input.readSignedLEShort();
		int tcycles = 0;
		input.skip(2);
		int ecycles = input.readSignedLEShort();

		String gameName = input.readZZTString(20);

		String[] flags = new String[16];
		for (int i = 0; i < flags.length; i++) {
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
		int stones = input.readSignedLEShort();
		input.skip(633);

		GameState state = new GameState(boards, ammo, gems, keys, health,
				startBoard, torches, tcycles, ecycles, score, gameName, flags,
				timePassed, isSave, stones);
		GameController game = new SuperZGameController(state);

		ArrayList<Board> boardsList = new ArrayList<Board>(boards);
		for (int i = 0; i <= boards; i++) {
			int boardLength = input.readSignedLEShort();
			byte[] b = new byte[boardLength];
			input.read(b, 0, boardLength);
			try {
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
		String boardName = input.readZZTString(60);

		Element[][][] elements = loadRLEBoard(game.getElementResolver(), input);

		int shots = input.read();
		// int dark = input.read();//Does not exist in SZZT
		int boardNorth = input.read();
		int boardSouth = input.read();
		int boardWest = input.read();
		int boardEast = input.read();
		int restart = input.read();
		String message = "";// not stored for SZZT
		int enterX = input.read();
		int enterY = input.read();
		input.skip(4); // "Camera X/Y"
		int timeLimit = input.readSignedLEShort();
		input.skip(14);
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

			if (statY > SuperZBoard.BOARD_HEIGHT || statX > SuperZBoard.BOARD_WIDTH) {
				System.err.println("Stat #" + (statsList.size() + 1)
						+ " Out of bounds stat: " + statX + "/" + statY);
				continue;
			}

			Stats s = new Stats(statX, statY, p1, p2, p3, stepX, stepY, cycle,
					follower, leader, currInstr, oopLength, oop);
			statsList.add(s);
			if (oopLength < 0) {
				delayedStats.add(s);
			} else {
				if (elements[statX][statY][0] != null) {
					elements[statX][statY][0].setStats(s);
				}
			}
			elements[statX][statY][1] = game.getElementResolver().resolve(underT, underC,
					statX, statY);

			if (j == 0) {
				playerX = statX;
				playerY = statY;
			}
		}

		mergeStatsList(elements, delayedStats, statsList);

		Board board = new SuperZBoard(elements, boardName, shots, 0, boardNorth,
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

			String oop = null;
			int oopOffset = Math.abs(s.getOopLength());// Do not adjust to zero
			// based or anything like that.
			// Probably because the ACE is always 0.
			if (statsList.size() > oopOffset) {
				oop = statsList.get(oopOffset).getOop();
				s = new Stats(s.getX(), s.getY(), s.getP1(), s.getP2(), s
						.getP3(), s.getStepX(), s.getStepY(), s.getCycle(), s
						.getFollower(), s.getLeader(), s.getCurrInstr(), s
						.getOopLength(), oop);
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
	private static Element[][][] loadRLEBoard(ElementResolver resolve, UnsignedDataInputStream input)
			throws IOException {
		Element[][][] elements = new Element[SuperZBoard.BOARD_WIDTH][SuperZBoard.BOARD_HEIGHT][SuperZBoard.BOARD_DEPTH];
		int x = 0, y = 0;
		for (int pos = 0; pos < SuperZBoard.BOARD_WIDTH
				* SuperZBoard.BOARD_HEIGHT;) {
			int reps = input.read();
			int type = input.read();
			int col = input.read();

			for (int i = 0; i < reps; i++) {
				if (pos == SuperZBoard.BOARD_WIDTH * SuperZBoard.BOARD_HEIGHT) {
					System.err.println("pos at limit");
					break;
				}
				elements[x][y][0] = resolve.resolve(type, col, x, y);
				if (elements[x][y][0] == null)
					System.err.println("Unresolved element @ " + x + "x" + y
							+ ": " + type + " $" + col);
				elements[x][y][1] = null;
				x++;
				if (x == SuperZBoard.BOARD_WIDTH) {
					y++;
					x = 0;
				}
				pos++;
			}
		}
		return elements;
	}

}
