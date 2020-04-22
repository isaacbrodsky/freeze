/**
 * 
 */
package com.isaacbrodsky.freeze.filehandling;

import java.io.IOException;

import com.isaacbrodsky.freeze.EmuMode;
import com.isaacbrodsky.freeze.utils.StringBackedInputStream;
import com.isaacbrodsky.freeze.utils.StringBackedOutputStream;

/**
 * @author isaac
 * 
 */
public interface WorldList {
	public String buildLoadMenu(String suffix);

	/**
	 * @param emu
	 * @return
	 */
	public String getDefaultWorld(EmuMode emu);

	/**
	 * @param file
	 * @return
	 */
	public StringBackedInputStream getWorldBytes(String file)
			throws IOException;

	/**
	 * @param bytes
	 */
	public void putWorldBytes(StringBackedOutputStream bytes, String file)
			throws IOException, UnsupportedOperationException;

	/**
	 * @param target
	 * @return
	 */
	public StringBackedInputStream getHiscoreBytes(String file, EmuMode emu)
			throws IOException;

	/**
	 * @param bytes
	 */
	public void putHiscoreBytes(StringBackedOutputStream bytes, String file,
			EmuMode emu) throws IOException, UnsupportedOperationException;
}
