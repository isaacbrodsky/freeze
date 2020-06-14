/**
 * 
 */
package com.isaacbrodsky.freeze2.filehandling;

import com.isaacbrodsky.freeze2.elements.SuperZElement;
import com.isaacbrodsky.freeze2.game.*;
import com.isaacbrodsky.freeze2.utils.StringBackedOutputStream;
import com.isaacbrodsky.freeze2.utils.UnsignedDataOutputStream;

import java.io.IOException;
import java.util.stream.Collectors;

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
		try (UnsignedDataOutputStream output = new UnsignedDataOutputStream(
				bytes)) {

			output.write(new byte[]{(byte) 0xFE, (byte) 0xFF});
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
			int startBoard = game.getBoardIndex();
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

			output.writeZZTString("FREEZE2  ", 9);
			output.writeZeroPadding(613);
			output.writeZZTString("FREEZE2  ", 9);

			for (int i = 0; i < game.getBoardList().size(); i++) {
				saveBoard(game.getBoardList().get(i), output);
			}

		}

		base.putWorldBytes(bytes, file);
	}

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

		obuf.writeSignedLEShort(b.getStats().size() - 1);
		for (Stat s : b.getStats()) {
			saveStats(s, obuf);
		}

		out.writeSignedLEShort(buf.getLength());
		out.write(buf.getData(), 0, buf.getLength());
	}

	/**
	 * 
	 */
	private void saveRLEBoardData(Board b, UnsignedDataOutputStream obuf)
			throws IOException {
		RleTile rle = null;
		for (Tile t : b.tileStream().collect(Collectors.toList())) {
			if (t == null) {
				// This is because of THIS engine glitching out.
				t = new Tile(SuperZElement.EMPTY, 0);
				System.err.println("null element on board "
						+ b.getState().boardName);
				// throw new IOException("Illegal null in RLE.");
			}
			if (rle == null) {
				rle = new RleTile(t);
			} else if (!rle.tile.equals(t) || rle.repeat == 255) {
				writeRleTile(rle, obuf);
				rle = new RleTile(t);
			} else {
				rle = rle.inc();
			}
		}

		writeRleTile(rle, obuf);
	}

	private static void writeRleTile(RleTile rle, UnsignedDataOutputStream obuf) throws IOException {
		obuf.write(rle.repeat);
		obuf.write(rle.tile.getType());
		obuf.write(rle.tile.getColor());
	}

	/**
	 * @param s
	 * @param obuf
	 * @throws IOException
	 */
	private void saveStats(Stat s,
			UnsignedDataOutputStream obuf) throws IOException {
		obuf.write(s.x);
		obuf.write(s.y);
		obuf.writeSignedLEShort(s.stepX);
		obuf.writeSignedLEShort(s.stepY);
		obuf.writeSignedLEShort(s.cycle);
		obuf.write(s.p1);
		obuf.write(s.p2);
		obuf.write(s.p3);

		obuf.writeLEShort(s.follower);
		obuf.writeLEShort(s.leader);

		Tile under = s.under;
		if (under == null) {
			obuf.write(0);
			obuf.write(0);
		} else {
			obuf.write(under.getType());
			obuf.write(under.getColor());
		}

		obuf.writeZeroPadding(4); // "pointer"

		obuf.writeSignedLEShort(s.currInstr);
		// Note: May actually be a negative number for binding
		obuf.writeSignedLEShort(s.oopLength);

		obuf.writeBytes(s.oop);
	}
}
