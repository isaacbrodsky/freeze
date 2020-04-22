/**
 * 
 */
package com.isaacbrodsky.freeze.elements.superz;

import java.util.List;

import com.isaacbrodsky.freeze.elements.Element;
import com.isaacbrodsky.freeze.elements.Player;
import com.isaacbrodsky.freeze.elements.ZObject;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.elements.oop.OOPHelpers;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.game.SuperZGameController;
import com.isaacbrodsky.freeze.graphics.ElementColoring;

/**
 * @author isaac
 * 
 */
public class SuperPlayer extends Player {
	@Override
	public void tick(GameController game, Board board) {
		if (game instanceof SuperZGameController)
			cm = ElementColoring.ColorMode.RECESSIVE;
		super.tick(game, board);

		if (!game.isPaused()) {
			Element under = board.floorAt(getX(), getY());
			if (under instanceof SuperWater) {
				SuperWater wa = (SuperWater) under;
				int x = getX(), y = getY();
				stepX = wa.getMoveX();
				stepY = wa.getMoveY();
				int dirOff = board.boundsCheck(x + stepX, y + stepY);
				if (dirOff >= 0) {
					handleEdgeOfBoard(game, board, dirOff);

					return;
				}

				Element e = board.elementAt(x + stepX, y + stepY);
				handleMoving(game, board, e);
			}
		}
	}

	@Override
	protected void takeItem(Board board, Element e) {
		board.removeAt(getX() + stepX, getY() + stepY, e);
		if (board.floorAt(getX() + stepX, getY() + stepY) == null)
			OOPHelpers.putEmpty(board, getX() + stepX, getY() + stepY);
	}

	@Override
	protected void newBoard(GameController game) {
		List<Element> l = game.getBoard().getElementsByType(ZObject.class);
		for (Element e : l)
			e.message(game, game.getBoard(), Message.ENTER);
	}
}
