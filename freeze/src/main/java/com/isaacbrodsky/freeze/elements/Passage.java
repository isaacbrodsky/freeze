/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import java.util.List;

import com.isaacbrodsky.freeze.elements.data.InteractionRule;
import com.isaacbrodsky.freeze.elements.data.InteractionRulesSet;
import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.graphics.ElementColoring;

/**
 * @author isaac
 * 
 */
public class Passage extends AbstractElement {
	private ElementColoring color;

	public Passage() {
		color = new ElementColoring(0xFE, ElementColoring.ColorMode.CODOMINANT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#createInstance(int, int)
	 */
	@Override
	public void createInstance(SaveData dat) {
		this.color = new ElementColoring(dat.getColor(),
				ElementColoring.ColorMode.CODOMINANT);
	}

	@Override
	public SaveData getSaveData() {
		return new SaveData(0x0B, color);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getColoring()
	 */
	@Override
	public ElementColoring getColoring() {
		return color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getCycle()
	 */
	@Override
	public int getCycle() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		return 240;
	}

	private static final InteractionRulesSet _IRS = new InteractionRulesSet(
			InteractionRule.BLOCKS_MOVE);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getInteractionsRules()
	 */
	@Override
	public InteractionRulesSet getInteractionsRules() {
		return _IRS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.elements.Element#message(com.isaacbrodsky.freeze
	 * .game.GameController, com.isaacbrodsky.freeze.game.Board,
	 * com.isaacbrodsky.freeze.elements.oop.Message)
	 */
	@Override
	public void message(GameController game, Board board, Message msg) {
		if (msg.isTouch()) {
			if (game.getBoardList().size() < getDestBoard()) {
				// this can happen especially if passages are coped into a new
				// world and their dest index is larger than the world

				game.reportError("Board ID " + getDestBoard() + " missing.\n\n"
						+ "The board could not be loaded\n"
						+ "or is missing from the world file.");
				return;
			}

			Board dest = game.getBoardList().get(getDestBoard());

			if (dest == null) {
				// Missing board; ERROR CASE
				// This represents an error in the loading process

				game.reportError("Board ID " + getDestBoard() + " missing.\n\n"
						+ "The board could not be loaded\n"
						+ "or is missing from the world file.");
				return;
			}

			List<Element> destPassages = dest.getElementsByType(Passage.class);
			int myColor = color.getCode();
			Passage p = null;
			for (Element e : destPassages) {
				if (e.getColoring().getCode() == myColor) {
					Passage p2 = (Passage) e;
					if (p != null) {
						if (p.getDestBoard() != game.getBoardIdx()
								&& p2.getDestBoard() == game.getBoardIdx()) {
							p = p2;
						} else if (p.getDestBoard() != game.getBoardIdx()) {
							p = p2;
						}
					} else {
						p = p2;
					}
				}
			}

			if (p != null) {
				// Note: this wipes out the element under the passage but who
				// would do that? --> Possible data loss.
				Player destPlayer = dest.getPlayer();
				dest.removeAt(destPlayer.getX(), destPlayer.getY());
				destPlayer.setXY(p.getX(), p.getY());
				dest.putElement(p.getX(), p.getY(), destPlayer);
			}

			game.setBoard(getDestBoard());
			game.setPaused(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#tick()
	 */
	@Override
	public void tick(GameController game, Board board) {
	}

	private int getDestBoard() {
		if (getStats() == null)
			return 0;
		else
			return getStats().getP3();
	}
}
