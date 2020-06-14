/**
 * 
 */
package com.isaacbrodsky.freeze2.game;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.isaacbrodsky.freeze2.EmuMode;
import com.isaacbrodsky.freeze2.elements.Elements;
import com.isaacbrodsky.freeze2.elements.SuperZElements;
import com.isaacbrodsky.freeze2.graphics.Renderer;

import java.util.List;

/**
 * @author isaac
 * 
 */
@JsonTypeName("SUPERZZT")
public class SuperZGameController extends ZGameController {
	public SuperZGameController(GameState state, List<Board> boards) {
		super(state, boards);
	}

	@Override
	public Elements getElements() {
		return SuperZElements.INSTANCE;
	}

	@Override
	public int getXViewOffset() {
		if (getBoard().getPlayer() == null) {
			return 0; // TODO
			// return SuperZBoard.BOARD_WIDTH / 2;
		}

		int px = getBoard().getPlayer().x - 30;
		px = Math.max(px, 0);
		if (px + Renderer.DISPLAY_WIDTH > getBoard().getWidth())
			px = getBoard().getWidth() - Renderer.DISPLAY_WIDTH;
		return px;
	}

	@Override
	public int getYViewOffset() {
		if (getBoard().getPlayer() == null) {
			return 0; // TODO
			// return SuperZBoard.BOARD_WIDTH / 2;
		}

		int py = getBoard().getPlayer().y - 12;
		py = Math.max(py, 0);
		if (py + Renderer.DISPLAY_HEIGHT > getBoard().getHeight())
			py = getBoard().getHeight() - Renderer.DISPLAY_HEIGHT;
		return py;
	}

	@Override
	public EmuMode getEmuMode() {
		return EmuMode.SUPERZZT;
	}

	@Override
	public int statsLimit() {
		return 128;
	}
}
