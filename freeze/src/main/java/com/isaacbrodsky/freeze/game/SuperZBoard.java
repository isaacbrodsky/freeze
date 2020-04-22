/**
 * 
 */
package com.isaacbrodsky.freeze.game;

import java.util.ArrayList;
import java.util.List;

import com.isaacbrodsky.freeze.elements.Element;
import com.isaacbrodsky.freeze.elements.Line;
import com.isaacbrodsky.freeze.elements.ZObject;
import com.isaacbrodsky.freeze.elements.data.InteractionRule;
import com.isaacbrodsky.freeze.elements.superz.Web;
import com.isaacbrodsky.freeze.filehandling.DefaultZWorldCreator;

/**
 * @author isaac
 * 
 */
public class SuperZBoard extends ZBoard implements Board {
	/**
	 * Width in elements of this board.
	 */
	public final static int BOARD_WIDTH = 96;
	/**
	 * Height in elements of this board.
	 */
	public final static int BOARD_HEIGHT = 80;
	/**
	 * Number of levels of depth of this board.
	 */
	public final static int BOARD_DEPTH = 2;

	private Element[][][] elements;

	private BoardState state;

	public SuperZBoard() {
		super();

		state = new BoardState();

		elements = new DefaultZWorldCreator().createDefaultElementSet();
		state.enterX = state.enterY = 3;
		setACE(elementAt(3, 3));
	}

	/**
	 * @param game
	 */
	public SuperZBoard(Element[][][] elements2, String boardName2, int shots2,
			int dark2, int boardNorth2, int boardSouth2, int boardWest2,
			int boardEast2, int restart2, String message2, int enterX2,
			int enterY2, int timeLimit2, int playerX2, int playerY2) {
		this();
		
		this.elements = elements2;

		this.state = new BoardState(boardName2, shots2, dark2, boardNorth2,
				boardSouth2, boardWest2, boardEast2, restart2, message2,
				enterX2, enterY2, timeLimit2);

		setACE(elementAt(playerX2, playerY2));

		for (Element iterE : getElementList()) {
			if (iterE instanceof Line)
				((Line) iterE).recalculateLineWalls(this);
			if (iterE instanceof Web)
				((Web) iterE).recalculateLineWalls(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SuperZZT Board [" + BOARD_WIDTH + "x" + BOARD_HEIGHT + "x"
				+ BOARD_DEPTH + "]\r\n" + state.toString();
	}

	/**
	 * Performs board ticking actions. Should be called as a part of the game
	 * simulation system.
	 */
	@Override
	public void tick() {
		super.tick();
	}

	/**
	 * @return
	 */
	@Override
	public Element[][][] getElements() {
		return elements;
	}

	/**
	 * 
	 */
	@Override
	public Element floorAt(int x, int y) {
		for (int d = 0; d < getDepth(); d++) {
			if (elements[x][y][d] != null) {
				if (elements[x][y][d].getInteractionsRules().is(
						InteractionRule.FLOOR))
					return elements[x][y][d];
			}
		}

		return null;
	}

	/**
	 * @param x
	 * @param y
	 * @param depth
	 * @return Non-floor element at the specified location and depth or
	 *         <code>null</code>
	 */
	@Override
	public Element elementAt(int x, int y, int d) {
		if (elements[x][y][d] == null)
			return null;
		if (!elements[x][y][d].getInteractionsRules().is(InteractionRule.FLOOR))
			return elements[x][y][d];

		return null;
	}

	/**
	 * @param x
	 * @param y
	 * @return First non-floor element at the specified location or
	 *         <code>null</code>
	 */
	@Override
	public Element elementAt(int x, int y) {
		for (int d = 0; d < getDepth(); d++) {
			if (elements[x][y][d] != null) {
				if (!elements[x][y][d].getInteractionsRules().is(
						InteractionRule.FLOOR))
					return elements[x][y][d];
			}
		}

		return null;
	}

	/**
	 * Calls <code>removeAt(int, int, Element)</code> with <code>null</code> as
	 * the third parameter.
	 * 
	 * @see #removeAt(int, int, Element)
	 * @param x
	 * @param y
	 */
	@Override
	public void removeAt(int x, int y) {
		removeAt(x, y, null);
	}

	/**
	 * Removes the element at the given location at the first depth level,
	 * moving the element in the second depth level up (if it exists).
	 * 
	 * <p>
	 * Third argument specifies the specific element to remove.
	 * 
	 * @param x
	 * @param y
	 * @param e
	 *            [Unused] specific element to remove.
	 */
	@Override
	public void removeAt(int x, int y, Element e) {
		if (e != null) {
			if (elements[x][y][0] != e) {
				if (elements[x][y][1] == e) {
					elements[x][y][1] = null;
				} else {
					return;
				}
			}
		}
		elements[x][y][0] = elements[x][y][1];
		elements[x][y][1] = null;
	}

	/**
	 * Adds the given element at the given location. If an element already
	 * exists there is it bumped to the second level. If an element existed
	 * there, it's removed from the board and returned to the caller. Returns
	 * <code>null</code> if no element was removed.
	 * 
	 * <p>
	 * This method does not notify the moved element that its X/Y coords have
	 * changes.
	 * 
	 * @param x
	 * @param y
	 * @param e
	 * @return
	 */
	@Override
	public Element putElement(int x, int y, Element e) {
		Element ret = null;
		if (elements[x][y][0] != null) {
			ret = elements[x][y][1];
			elements[x][y][1] = elements[x][y][0];
		}
		elements[x][y][0] = e;
		return ret;
	}

	/**
	 * Returns a list of elements on the board who have the given class (does
	 * not check for subclass or implementing).
	 * 
	 * <p>
	 * Performance: Calls {@link #getElementList()}. Also iterates over all
	 * elements on the board.
	 * 
	 * @param clazz
	 * @return
	 */
	@Override
	public List<Element> getElementsByType(Class clazz) {
		ArrayList<Element> eList = new ArrayList<Element>();
		for (Element e : getElementList())
			if (e.getClass().equals(clazz))
				eList.add(e);

		return eList;
	}

	/**
	 * Returns a list of all elements on the board.
	 * 
	 * <p>
	 * Performance: Iterates over all elements on the board.
	 * 
	 * @return
	 */
	@Override
	public List<Element> getElementList() {
		ArrayList<Element> eList = new ArrayList<Element>();
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				for (int d = 0; d < getDepth(); d++) {
					if (elements[x][y][d] != null)
						eList.add(elements[x][y][d]);
				}
			}
		}
		return eList;
	}

	/**
	 * Retrieves all elements that match the given name on this board. Names are
	 * compared case insensitively. It returns an empty list if no such elements
	 * exist.
	 * 
	 * <p>
	 * Performance: This method iterates over all board objects. Not so good.
	 * 
	 * @param name
	 * @return
	 */
	@Override
	public List<Element> getElementsByName(String name) {
		ArrayList<Element> ret = new ArrayList<Element>();
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				Element e = elementAt(x, y);

				if (e instanceof ZObject) {
					ZObject zobje = (ZObject) e;
					if (zobje.getName() != null) {
						if (zobje.getName().trim().equalsIgnoreCase(name)) {
							ret.add(e);
						}
					}
				}
			}
		}

		return ret;
	}

	@Override
	public BoardState getState() {
		return state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.game.Board#getDepth()
	 */
	@Override
	public int getDepth() {
		return BOARD_DEPTH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.game.Board#getHeight()
	 */
	@Override
	public int getHeight() {
		return BOARD_HEIGHT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.game.Board#getWidth()
	 */
	@Override
	public int getWidth() {
		return BOARD_WIDTH;
	}
}
