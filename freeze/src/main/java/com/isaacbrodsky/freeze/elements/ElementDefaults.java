/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.isaacbrodsky.freeze.EmuMode;
import com.isaacbrodsky.freeze.elements.data.Stats;

/**
 * @author isaac
 * 
 */
public final class ElementDefaults {
	private ElementResolver resolve;

	public ElementDefaults(EmuMode emu) {
		// if (emu.equals(EmuMode.ZZT)) {
		// Background - Foreground
		_aIR(0x00, 0x70);// EMPTY #00
		_aIR(0x04, 0x1F); // PLAYER #04
		_aIR(0x05, 0x03); // AMMO #05
		_aIR(0x06, 0x06); // TORCH #06
		_aIR(0x0A, 0x0F); // SCROLL #10
		_aIR(0x0E, 0x05); // ENERGIZER #14
		_aIR(0x0F, 0x0F); // STAR #15
		_aIR(0x12, 0x07); // BULLET #18
		_aIR(0x14, 0x20); // FOREST #20
		_aIR(0x20, 0x0A); // RICOCHET #32
		_aIR(0x22, 0x06); // BEAR #34
		_aIR(0x23, 0x0D); // RUFFIAN #35
		_aIR(0x26, 0x07); // SHARK #38
		_aIR(0x29, 0x0C); // LION #41
		_aIR(0x2A, 0x0B); // TIGER #42)

		// _aIR(duplicator, 0xFE); //DUPLICATOR #
		// ricochet, 0x0A

		_aSE(0x04);
		_aSE(0x06);
		//_aSE(0x07); - WRONG
		_aSE(0x10); //"clockwise"
		_aSE(0x11); //"counter"
		_aSE(0x0A);
		_aSE(0x0B);
		_aSE(0x0C);
		_aSE(0x0D);
		_aSE(0x1D);
		_aSE(0x27); //spinninggun
		_aSE(0x2C);
		_aSE(0x2D);
		// }

		resolve = new ElementResolver(emu);
	}

	/**
	 * Add integer resolver
	 * 
	 * @param k
	 * @param value
	 */
	private void _aIR(int k, int v) {
		_iresolver.put(k, v);
	}

	private void _aSE(int k) {
		_stats.add(k);
	}

	private final Map<Integer, Integer> _iresolver = new HashMap<Integer, Integer>();
	private final Set<Integer> _stats = new HashSet<Integer>();

	public Stats getDefaultStats(String what) {
		int type = resolve.codeFromName(what);
		return getDefaultStats(type);
	}

	public Stats getDefaultStats(int type) {
		if (_stats.contains(type)) {
			return new Stats();
//			return true;
		}
		return null;
	}

	public int getDefaultColor(String what) {
		int type = resolve.codeFromName(what);
		return getDefaultColor(type);
	}

	public int getDefaultColor(int type) {
		if (_iresolver.containsKey(type)) {
			return _iresolver.get(type);
		}

		return -1;
	}

	/**
	 * @param what
	 * @param code
	 * @return
	 */
	public int getColor(String what, int code) {
		int type = resolve.codeFromName(what);
		return getColor(type, code);
	}

	/**
	 * @param toType
	 * @param toCodeActual
	 * @return
	 */
	public int getColor(int type, int code) {
		int nCode = getDefaultColor(type);
		if (nCode != -1)
			return nCode;

		return code;
	}
}
