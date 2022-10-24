/**
 * 
 */
package com.isaacbrodsky.freeze2.filehandling;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import com.isaacbrodsky.freeze2.EmuMode;
import com.isaacbrodsky.freeze2.utils.StringBackedInputStream;
import com.isaacbrodsky.freeze2.utils.StringBackedOutputStream;

/**
 * @author isaac
 * 
 */
public class LocalWorldList implements WorldList {

	private final File baseFile;

	/**
	 * @param base
	 */
	public LocalWorldList(String base) {
		this.baseFile = new File(base);
	}

	public String buildLoadMenu(String suffix) {
		StringBuilder loadSelects = new StringBuilder();
		if (baseFile.listFiles() != null) {
			for (File f2 : baseFile.listFiles()) {
				// TODO: Fix locale issues
				loadSelects.append(f2.getName().toUpperCase().substring(0,
						f2.getName().length() - 1 - suffix.length())
						+ "\r\n");
			}
		}

		return loadSelects.toString();
	}

	private File getChildOfBase(String name) {
		for (File f : baseFile.listFiles((dir, name2) -> name.equalsIgnoreCase(name2))) {
			return f;
		}
		return null;
	}

	/**
	 * @param emu
	 * @return
	 */
	public String getDefaultWorld(EmuMode emu) {
		switch (emu) {
		case ZZT:
			File f = getChildOfBase("ZZT.CFG");
			if (f.exists() && f.isFile() && f.canRead()) {
				try (InputStream in = new FileInputStream(f)) {
					byte[] buf = new byte[8];
					in.read(buf, 0, 8);

					String ret = new String(buf, Charset.forName("US-ASCII"));
					return ret.trim();
				} catch (Exception e) {
					return null;
				}
			}
			return null;
		case SUPERZZT:
			return "MONSTER";
		default:
		}

		return null;
	}

	/**
	 * @param file
	 * @return
	 */
	public StringBackedInputStream getWorldBytes(String file)
			throws IOException {
		return new StringBackedInputStream(new FileInputStream(
				getChildOfBase(file)));
	}

	/**
	 * @param bytes
	 */
	public void putWorldBytes(StringBackedOutputStream bytes, String file)
			throws IOException {
		try (FileOutputStream fos = new FileOutputStream(getChildOfBase(file))) {
			fos.write(bytes.getData(), 0, bytes.getLength());
		}
	}

	/**
	 * @param target
	 * @return
	 */
	public StringBackedInputStream getHiscoreBytes(String file, EmuMode emu)
			throws IOException {
		File target = getChildOfBase(file + "." + emu.getHiscoreFileSuffix());
		if (!(target.isFile() && target.canRead())) {
			return null;
		}

		return new StringBackedInputStream(new FileInputStream(target));
	}

	/**
	 * @param bytes
	 */
	public void putHiscoreBytes(StringBackedOutputStream bytes, String file,
			EmuMode emu) throws IOException {
		FileOutputStream fos = new FileOutputStream(getChildOfBase(file + "."
				+ emu.getHiscoreFileSuffix()));

		fos.write(bytes.getData(), 0, bytes.getLength());

		fos.close();
	}
}
