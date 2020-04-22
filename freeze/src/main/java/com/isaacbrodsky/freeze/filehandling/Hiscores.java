/**
 * 
 */
package com.isaacbrodsky.freeze.filehandling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import com.isaacbrodsky.freeze.EmuMode;
import com.isaacbrodsky.freeze.menus.Menu;
import com.isaacbrodsky.freeze.menus.MenuCallback;
import com.isaacbrodsky.freeze.menus.Menu.SendMode;
import com.isaacbrodsky.freeze.utils.SortUtils;
import com.isaacbrodsky.freeze.utils.StringBackedInputStream;
import com.isaacbrodsky.freeze.utils.StringBackedOutputStream;
import com.isaacbrodsky.freeze.utils.UnsignedDataInputStream;
import com.isaacbrodsky.freeze.utils.UnsignedDataOutputStream;

/**
 * This class represents a hiscore (highscore?) list. It is used for loading,
 * displaying, modifiying (only insertion supported) and saving hiscore lists in
 * both ZZT and SuperZZT compatible formats.
 * 
 * <p>
 * (Format definition at end of this Javadoc.) ZZT hiscores are stored (for a
 * game named WOOF) in the file <code>WOOF.HI</code>. SuperZZT scores would be
 * stored in <code>WOOF.HGZ</code>.
 * 
 * <p>
 * ZZT generated hiscore lists may include garbage values in its files (which
 * are hidden from the player because the length field is set to 0.) This class
 * does not maintain those garbage values. A hiscore list loaded and saved
 * through this class will only contain valid data and nulls (0x00).
 * 
 * <p>
 * The "name" field in the hiscore file is actually quite a bit larger than the
 * game supports entering after a game. This class is only limited by the
 * limitations of the file format. As a result, names saved through this class
 * may be larger than ZZT can properly display, resulting in minor (and
 * ultimately harmless) graphical corruption.
 * 
 * <p>
 * Interestingly SuperZZT will not display the entirety of the name field, and
 * will in fact truncate it when displaying from the main menu. This truncation
 * point is quite a bit after the length allowed by SuperZZT itself so most
 * names should be fine and unaffected by this quirk. I do not believe that
 * SuperZZT actually destroys long names when stored in the file.
 * 
 * <p>
 * The ZZT and SuperZZT formats are mostly identical, except that SuperZZT's HGZ
 * format is capable of handling names up to 60 characters, as opposed to ZZT's
 * 50. (Technically it would probably be possible to trick ZZT into reading past
 * the end of a name but this would be obnoxous.)
 * 
 * <table>
 * <tr>
 * <th>ZZT Hiscore File (.hi)</th>
 * <th>(1 Entry repeated *30)</th>
 * <th>(53 bytes total)</th>
 * </tr>
 * <tr>
 * <th>Offset</th>
 * <th>Size</th>
 * <th>Description (Type)</th>
 * </tr>
 * <tr>
 * <td>0</td>
 * <td>1</td>
 * <td>Size of name (Byte/ Pascal String Size)</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>50</td>
 * <td>Name (Pascal String)</td>
 * </tr>
 * <tr>
 * <td>51</td>
 * <td>2</td>
 * <td>Score (Signed Int)</td>
 * </tr>
 * <tr>
 * <th>SuperZZT Hiscore File (.hgz)</th>
 * <th>(1 Entry repeated *30)</th>
 * <th>(63 bytes total)</th>
 * </tr>
 * <tr>
 * <th>Offset</th>
 * <th>Size</th>
 * <th>Description (Type)</th>
 * </tr>
 * <tr>
 * <td>0</td>
 * <td>1</td>
 * <td>Size of name (Byte/ Pascal String Size)</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>60</td>
 * <td>Name (Pascal String)</td>
 * </tr>
 * <tr>
 * <td>61</td>
 * <td>2</td>
 * <td>Score (Signed Int)</td>
 * </tr>
 * </table>
 * 
 * @author isaac
 */
public class Hiscores {
	/**
	 * Number of hiscore entries per file.
	 * <p>
	 * I figure it's best to stick to the limit since ZZT is probably doing a
	 * fixed size read. (Haven't tested or anything)
	 */
	private static final int LIMIT = 30;

	/**
	 */
	private final String game;
	private final WorldList base;

	/**
	 * Represents whether this hiscores list has data, not whether it has been
	 * loaded from file. Even if the data in the list is blank this may still be
	 * true- {@link #entries} is gaurenteed to not be <code>null</code>.
	 */
	private boolean loaded;

	/**
	 * The hiscore entries.
	 */
	private ArrayList<HiscoreEntry> entries;

	/**
	 * The emulation mode this hiscores object is working with. Mode selects the
	 * format and filename suffix used by this object.
	 */
	private EmuMode emu;

	/**
	 * This represents the bytes for a blank entry in this hiscores list. Since
	 * different modes have different formats this may be different for
	 * different files.
	 */
	private final byte[] blankEntry;

	/**
	 * Constructs a hiscore list. To load data into this list, {@link #load()}
	 * should be called. This list will be blank otherwise.
	 * 
	 * @param base
	 * @param game
	 */
	public Hiscores(EmuMode emu, WorldList base, String game) {
		this(emu, base, game, new ArrayList<HiscoreEntry>());

		loaded = false;
	}

	/**
	 * Constructs a hiscore list for the given game with existing data.
	 * 
	 * @param emu
	 * @param base
	 * @param game
	 * @param entries
	 */
	public Hiscores(EmuMode emu, WorldList base, String game,
			ArrayList<HiscoreEntry> entries) {
		this.base = base;
		this.game = game;
		this.entries = new ArrayList<HiscoreEntry>(entries);

		loaded = true;
		this.emu = emu;

		blankEntry = new byte[emu.getHiscoreNameSize() + 3];
		for (int i = 0; i < blankEntry.length; i++)
			blankEntry[i] = 0;
	}

	/**
	 * Determines the name of the file to load this game's hiscores from and
	 * loads. If this hiscores list previously had data the data from the file
	 * is appended to the end of this list.
	 * 
	 * <p>
	 * Garbage data in the loaded file is not preserved when loaded.
	 * 
	 * @throws IOException
	 */
	public void load() throws IOException {
		loaded = true;
		StringBackedInputStream bytes = base.getHiscoreBytes(game, emu);
		if (bytes == null) {
			return;
		}

		UnsignedDataInputStream input = new UnsignedDataInputStream(bytes);

		for (int i = 0; i < LIMIT; i++) {
			try {
				String name = input.readZZTString(emu.getHiscoreNameSize());
				int score = input.readSignedLEShort();

				if (name.length() == 0)
					continue;

				HiscoreEntry en = new HiscoreEntry(name, score);
				entries.add(en);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}

		input.close();
	}

	/**
	 * Determines the name of the file to save this game's hiscores to and
	 * saves. Existing data at that location is overwritten.
	 * 
	 * <p>
	 * If there are more than {@link #LIMIT} entries in this list, only the
	 * first <code>LIMIT</code> will be saved.
	 * 
	 * <p>
	 * Scores above 2<sup>15</sup> or below 0 may result in strange behavior.
	 * 
	 * <p>
	 * Names longer than the name size in the file will be truncated to fit.
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException {
		StringBackedOutputStream bytes = new StringBackedOutputStream();
		UnsignedDataOutputStream output = new UnsignedDataOutputStream(
				bytes);

		for (int i = 0; i < LIMIT; i++) {
			if (i < entries.size()) {
				HiscoreEntry e = entries.get(i);

				e.writeOut(output);
			} else {
				output.write(blankEntry);
			}
		}

		output.close();
		
		base.putHiscoreBytes(bytes, game, emu);
	}

	/**
	 * Creates a menu display of this hiscores list. Attempts to load from file
	 * if the list was created with no data and load has not been called.
	 * 
	 * @param callback
	 * @return
	 */
	public Menu show(MenuCallback callback) {
		try {
			if (!loaded)
				load();
		} catch (Exception e) {
			return new Menu("Hiscores Error", "Could not open hiscores\r\n"
					+ e.toString(), null);
		}

		if (entries.size() == 0)
			return new Menu("Hiscores for " + game, "No scores!", null);

		StringBuilder sb = new StringBuilder();

		boolean first = true;
		for (HiscoreEntry e : entries) {
			if (!first)
				sb.append("\r\n");
			else
				first = false;

			String ss = String.valueOf(e.getScore());
			while (ss.length() < 5)
				ss = " " + ss;

			sb.append(ss).append(": ").append(e.getName());
		}

		return new Menu("Hiscores for " + game, sb.toString(), callback,
				SendMode.LASTLINE);
	}

	/**
	 * Creates a new hiscores based on this existing list with the given entry
	 * inserted into it.
	 * 
	 * <p>
	 * The resulting hiscores object will be sorted, and truncated to only have
	 * {@link #LIMIT} entries. The newest entry is thus not guaranteed to be in
	 * the new list.
	 * 
	 * @param name
	 * @param score
	 * @return
	 */
	public Hiscores insert(HiscoreEntry entry) {
		ArrayList<HiscoreEntry> nEntries = new ArrayList<HiscoreEntry>(entries);

		SortUtils.quickSort(nEntries);
		Collections.reverse(nEntries);

		for (int i = 0; i < LIMIT; i++) {
			if (i >= nEntries.size()) {
				nEntries.add(entry);
				break;
			}
			if (nEntries.get(i).compareTo(entry) <= 0) {
				nEntries.add(i, entry);
				break;
			}
		}

		while (nEntries.size() > LIMIT) {
			nEntries.remove(nEntries.size() - 1);
		}

		return new Hiscores(emu, base, game, nEntries);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("** Hiscores");
		sb.append("\r\nBase: ").append(base);
		sb.append("\r\nGame: ").append(game);
		for (HiscoreEntry e : entries)
			sb.append("\r\n").append(e);
		return sb.toString();
	}

	/**
	 * Represents an entry in a hiscore list for a world.
	 * 
	 * <p>
	 * Immutable.
	 * 
	 * @author isaac
	 */
	public final class HiscoreEntry implements Comparable<HiscoreEntry> {
		private final String name;
		private final int score;

		/**
		 * Constructs a new HiscoreEntry with the given data. Name cannot be
		 * <code>null</code>.
		 * 
		 * @param name
		 * @param score
		 */
		public HiscoreEntry(final String name, final int score) {
			this.name = name;
			this.score = score;
		}

		/**
		 * Silently truncates the name if it is longer than the limit.
		 * 
		 * @param output
		 */
		public void writeOut(UnsignedDataOutputStream output)
				throws IOException {
			int sz = emu.getHiscoreNameSize();
			String sn = name;
			if (name.length() > sz)
				sn = name.substring(0, sz);
			output.writeZZTString(sn, sz);
			output.writeSignedLEShort(score);
		}

		/**
		 * Gets the name... duh.
		 * 
		 * @return
		 */
		public String getName() {
			return name;
		}

		/**
		 * Gets the score... duh.
		 * 
		 * @return
		 */
		public int getScore() {
			return score;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(score).append(": ").append(name);
			return sb.toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof HiscoreEntry))
				return false;
			HiscoreEntry other = (HiscoreEntry) o;
			return score == other.getScore();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return Integer.valueOf(score).hashCode();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(HiscoreEntry o) {
			HiscoreEntry other = (HiscoreEntry) o;
			return Integer.valueOf(score).compareTo(
					Integer.valueOf(other.getScore()));
		}
	}
}
