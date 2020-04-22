/**
 * 
 */
package com.isaacbrodsky.freeze.elements.superz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.isaacbrodsky.freeze.elements.*;
import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.elements.oop.OOPHelpers;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.graphics.ElementColoring;

/**
 * @author isaac
 * 
 */
public class Spider extends Lion {
	private SaveData dat;
	private ElementColoring color;

	@Override
	public void createInstance(SaveData dat) {
		this.color = new ElementColoring(dat.getColor(),
				ElementColoring.ColorMode.RECESSIVE);
		this.dat = dat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getSaveData()
	 */
	@Override
	public SaveData getSaveData() {
		return dat;
	}

	@Override
	public ElementColoring getColoring() {
		return color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		return 15;
	}

	private static final int[][] MOVES = { { 1, 0, }, { -1, 0 }, { 0, -1 },
			{ 0, 1 } };

	protected void handleMove(GameController game, Board board,
			String moveDirStr) {
		ArrayList<int[]> untried = new ArrayList<int[]>(Arrays.asList(MOVES));

		boolean first = moveDirStr.equals("seek");
		boolean moved = false;

		while (untried.size() > 0 && !moved) {
			int movX, movY;
			if (first) {
				int moveDir = OOPHelpers.getDirFromStringArray(game, board,
						this, new String[] { moveDirStr });
				movX = OOPHelpers.getDirX(moveDir);
				movY = OOPHelpers.getDirY(moveDir);
				first = false;
			} else {
				int idx = new Random().nextInt(untried.size());
				int[] mov = untried.get(idx);
				movX = mov[0];
				movY = mov[1];
				untried.remove(idx);
			}

			if (board.boundsCheck(getX() + movX, getY() + movY) != -1)
				continue;

			Element at = board.elementAt(getX() + movX, getY() + movY);
			if (at instanceof Player) {
				message(game, board, Message.TOUCH);
				board.removeAt(getX(), getY(), this);
				return;
			}
			Element web = board.floorAt(getX() + movX, getY() + movY);
			if (!(web instanceof Web)) {
				continue;
			}

			OOPHelpers.tryMove(getX(), getY(), movX, movY, board, this, false);
			moved = true;
		}
	}
}
