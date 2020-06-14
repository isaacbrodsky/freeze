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
import java.nio.file.Path;

import com.isaacbrodsky.freeze2.EmuMode;
import com.isaacbrodsky.freeze2.utils.StringBackedInputStream;
import com.isaacbrodsky.freeze2.utils.StringBackedOutputStream;

/**
 * @author isaac
 * 
 */
public class LocalWorldList implements WorldList {

	private final String base;
	private final Path basePath;

	/**
	 * @param base
	 */
	public LocalWorldList(String base) {
		this.base = base;
		this.basePath = new File(base).toPath();
	}

	public String buildLoadMenu(String suffix) {
		StringBuilder loadSelects = new StringBuilder();
		File f = new File(base);
		if (f.listFiles() != null)
		{
			for (File f2 : f.listFiles()) {
				if (f2.getName().toLowerCase().endsWith("." + suffix)) {
					loadSelects.append(f2.getName().toUpperCase().substring(0,
							f2.getName().length() - 1 - suffix.length())
							+ "\r\n");
				}
			}
		}

		return loadSelects.toString();
	}

	/**
	 * @param emu
	 * @return
	 */
	public String getDefaultWorld(EmuMode emu) {
		switch (emu) {
		case ZZT:
			File f = new File(base + "ZZT.CFG");
			if (f.exists() && f.isFile() && f.canRead()) {
				try {
					InputStream in = new FileInputStream(f);

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

	public File fileFromName(String file) {
		// TODO total hack due to originally developing on a case-insensitive FS
		// and not wanting to rewrite all the file handling to not mangle names.
		// This corrects for possible capitalizations of the file suffix.

		final int lastDot = file.lastIndexOf('.');
		final String suffix = file.substring(lastDot + 1);
		final String withoutSuffix = file.substring(0, lastDot + 1);

		final File lower = new File(withoutSuffix + suffix.toLowerCase());
		if (lower.exists()) {
			return lower;
		}
		return new File(withoutSuffix + suffix.toUpperCase());
	}

	/**
	 * @param file
	 * @return
	 */
	public StringBackedInputStream getWorldBytes(String file)
			throws IOException {
		return new StringBackedInputStream(new FileInputStream(
				fileFromName(file)));
	}

	/**
	 * @param bytes
	 */
	public void putWorldBytes(StringBackedOutputStream bytes, String file)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(fileFromName(file));

		fos.write(bytes.getData(), 0, bytes.getLength());

		fos.close();
	}

	/**
	 * @param target
	 * @return
	 */
	public StringBackedInputStream getHiscoreBytes(String file, EmuMode emu)
			throws IOException {
		File target = fileFromName(file + "." + emu.getHiscoreFileSuffix());
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
		FileOutputStream fos = new FileOutputStream(fileFromName(file + "."
				+ emu.getHiscoreFileSuffix()));

		fos.write(bytes.getData(), 0, bytes.getLength());

		fos.close();
	}
}
