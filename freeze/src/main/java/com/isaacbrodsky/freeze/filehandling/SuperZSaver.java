/**
 * 
 */
package com.isaacbrodsky.freeze.filehandling;

import java.io.IOException;
import java.util.ArrayList;

import com.isaacbrodsky.freeze.elements.Element;
import com.isaacbrodsky.freeze.elements.Empty;
import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.data.Stats;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.game.SuperZGameController;
import com.isaacbrodsky.freeze.utils.StringBackedOutputStream;
import com.isaacbrodsky.freeze.utils.UnsignedDataOutputStream;

/**
 * @author isaac
 * 
 */
public class SuperZSaver implements Saver {
	public void save(GameController inGame, WorldList base, String file) throws IOException {
		if (!(inGame instanceof SuperZGameController)) {
			throw new IllegalArgumentException(
					"This class only supports SuperZGameControllers");
		}

		SuperZGameController game = (SuperZGameController) inGame;

		StringBackedOutputStream bytes = new StringBackedOutputStream();
		UnsignedDataOutputStream output = new UnsignedDataOutputStream(
				bytes);

		output.write(new byte[] { (byte) 0xFE, (byte) 0xFF });
		int boards = game.getBoardList().size();
		output.writeSignedLEShort(boards - 1);
		int ammo = game.getState().ammo;
		output.writeSignedLEShort(ammo);
		int gems = game.getState().gems;
		output.writeSignedLEShort(gems);
		int[] keys = new int[game.getState().keys.length];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = game.getState().keys[i];
			output.write(keys[i]);
		}
		int health = game.getState().health;
		output.writeSignedLEShort(health);
		int startBoard = game.getBoardIdx();
		output.writeSignedLEShort(startBoard);
		int torches = game.getState().torches;
		output.writeSignedLEShort(torches);// Dummy
		int score = game.getState().score;
		output.writeSignedLEShort(score);
		int tcycles = game.getState().tcycles;
		output.writeSignedLEShort(tcycles);// Dummy
		int ecycles = game.getState().ecycles;
		output.writeSignedLEShort(ecycles);

		// output.writeZeroPadding(2); // UNK * 2

		String gameName = game.getState().gameName;
		output.writeZZTString(gameName, 20);

		String[] flags = new String[16];
		for (int i = 0; i < 16; i++) {
			flags[i] = game.getState().flags[i];
			if (flags[i] == null)
				flags[i] = "";

			output.writeZZTString(flags[i], 20);
		}

		int timePassed = game.getState().timePassed;
		output.writeSignedLEShort(timePassed);
		/*
		 * "This stores a runtime memory location that points to the status
		 * element currently being used by the player. Its value is not used
		 * when loading worlds, and is always set to 0 when saving worlds."
		 */
		output.writeZeroPadding(2);
		int isSave = game.getState().isSave;
		output.write(isSave);
		output.writeSignedLEShort(game.getState().stones);

		output.writeZZTString("FREEZE   ", 9);
		output.writeZeroPadding(613);
		output.writeZZTString("FREEZE   ", 9);

		for (int i = 0; i < game.getBoardList().size(); i++) {
			saveBoard(game.getBoardList().get(i), output);
		}

		output.close();

		base.putWorldBytes(bytes, file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.filehandling.Saver#saveBoard(com.isaacbrodsky
	 * .freeze.game.Board,
	 * com.isaacbrodsky.freeze.utils.UnsignedDataOutputStream)
	 */
	@Override
	public void saveBoard(Board b, UnsignedDataOutputStream out)
			throws IOException {
		if (b == null) { // board was not loaded correctly in the first place
			b = new DefaultSuperZWorldCreator().createDefaultBoard();
			// TODO add a message
		}

		StringBackedOutputStream buf = new StringBackedOutputStream(10240);
		UnsignedDataOutputStream obuf = new UnsignedDataOutputStream(buf);

		obuf.writeZZTString(b.getState().boardName, 60);

		saveRLEBoardData(b, obuf);

		obuf.write(b.getState().shots);
		obuf.write(b.getState().boardNorth);
		obuf.write(b.getState().boardSouth);
		obuf.write(b.getState().boardWest);
		obuf.write(b.getState().boardEast);
		obuf.write(b.getState().restart);
		obuf.write(b.getState().enterX);
		obuf.write(b.getState().enterY);
		obuf.writeZeroPadding(4);

		obuf.writeSignedLEShort(b.getState().timeLimit);

		obuf.writeZeroPadding(14);

		ArrayList<Stats> statsList = new ArrayList<Stats>();
		for (Element e : b.getElementList()) {
			if (e.getStats() != null) {
				statsList.add(e.getStats());
				if (e.getStats().getX() != e.getX()
						|| e.getStats().getY() != e.getY()) {
					System.err.println("Lying stats: "
							+ e.getClass().getSimpleName());
				}
			}
		}
		Stats pStats = b.getPlayer().getStats();
		statsList.remove(pStats);
		statsList.add(0, pStats);

		obuf.writeSignedLEShort(statsList.size() - 1);
		for (Stats s : statsList) {
			saveStats(s, b, statsList, obuf);
		}

		out.writeSignedLEShort(buf.getLength());
		out.write(buf.getData(), 0, buf.getLength());
	}

	/**
	 * 
	 */
	private void saveRLEBoardData(Board b, UnsignedDataOutputStream obuf)
			throws IOException {
		// Not using RLE for now just to simplify things
		int x = 0, y = 0;
		for (int pos = 0; pos < b.getWidth() * b.getHeight(); pos++) {
			Element e = b.elementAt(x, y);
			if (e == null)
				e = b.floorAt(x, y);
			if (e == null) {
				// This is because of THIS engine glitching out.
				e = new Empty();
				e.createInstance(new SaveData(0, 0x00));
				System.err.println("null element on board "
						+ b.getState().boardName + " x/y=" + x + "/" + y);
				// throw new IOException("Illegal null in RLE.");
			}

			obuf.write(1);// 1 repetition
			obuf.write(e.getSaveData().getType());
			obuf.write(e.getSaveData().getColor());

			x++;
			if (x == b.getWidth()) {
				x = 0;
				y++;
			}
		}
	}

	/**
	 * @param s
	 * @param statsList
	 *            Needed because some stats must reference other stats.
	 * @param obuf
	 * @throws IOException
	 */
	private void saveStats(Stats s, Board b, ArrayList<Stats> statsList,
			UnsignedDataOutputStream obuf) throws IOException {
		obuf.write(s.getX() + 1);
		obuf.write(s.getY() + 1);
		obuf.writeSignedLEShort(s.getStepX());
		obuf.writeSignedLEShort(s.getStepY());
		obuf.writeSignedLEShort(s.getCycle());
		obuf.write(s.getP1());
		obuf.write(s.getP2());
		obuf.write(s.getP3());

		// TODO follower
		obuf.writeLEShort(-1);
		// TODO leader
		obuf.writeLEShort(-1);

		Element under = b.getElements()[s.getX()][s.getY()][1];
		if (under == null) {
			obuf.write(0);
			obuf.write(0);
		} else {
			obuf.write(under.getSaveData().getType());
			obuf.write(under.getSaveData().getColor());
		}

		obuf.writeZeroPadding(4); // "pointer"

		obuf.writeSignedLEShort(s.getCurrInstr());
		// TODO doesn't support OOP binding properly;
		// just dumps the OOP text into each element
		obuf.writeSignedLEShort(s.getOop().length());

		if (s.getOopLength() > 0)
			obuf.writeBytes(s.getOop());
	}
}
