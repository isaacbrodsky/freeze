/**
 * 
 */
package com.isaacbrodsky.freeze;

import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.game.SuperZGameController;
import com.isaacbrodsky.freeze.game.ZGameController;

public enum EmuMode {
	/**
	 * Bad value
	 */
	DEFAULT, /**
	 * Reserved
	 */
	IOU, /**
	 * ZZT 3.2
	 */
	ZZT, /**
	 * SuperZZT
	 */
	SUPERZZT;

	public static EmuMode fromGame(GameController g) {
		if (g instanceof ZGameController)
			return ZZT;
		else if (g instanceof SuperZGameController)
			return SUPERZZT;
		return null;
	}
	
	/**
	 * @return
	 */
	public String getWorldFileSuffix() {
		switch (this) {
		case ZZT:
			return "zzt";
		case SUPERZZT:
			return "szt";
		case IOU:
			return "iou";
		case DEFAULT:
		default:
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getHiscoreFileSuffix() {
		switch (this) {
		case ZZT:
			return "hi";
		case SUPERZZT:
			return "hgz";
		case IOU:
			return "ihi";
		case DEFAULT:
		default:
		}
		return null;
	}

	/**
	 * @param emu
	 * @return
	 */
	public int getHiscoreNameSize() {
		switch (this) {
		case ZZT:
			return 50;
		case SUPERZZT:
			return 60;
		case IOU:
			return 60;
		case DEFAULT:
		default:
		}
		return -1;
	}

}