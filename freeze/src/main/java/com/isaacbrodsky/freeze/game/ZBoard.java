/**
 * 
 */
package com.isaacbrodsky.freeze.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isaacbrodsky.freeze.elements.Element;
import com.isaacbrodsky.freeze.elements.Line;
import com.isaacbrodsky.freeze.elements.Player;
import com.isaacbrodsky.freeze.elements.ZObject;
import com.isaacbrodsky.freeze.elements.data.InteractionRule;
import com.isaacbrodsky.freeze.filehandling.DefaultZWorldCreator;

/**
 * @author isaac
 * 
 */
public class ZBoard implements Board {
	/**
	 * Width in elements of this board.
	 */
	public final static int BOARD_WIDTH = 60;
	/**
	 * Height in elements of this board.
	 */
	public final static int BOARD_HEIGHT = 25;
	/**
	 * Number of levels of depth of this board.
	 */
	public final static int BOARD_DEPTH = 2;

	private Element ace;

	private Element[][][] elements;

	private BoardState state;

	/**
	 * Internal
	 */
	private Map<Element, Integer> conveyed;

	private int messageTime;

	public ZBoard() {
		messageTime = 0;
		conveyed = new HashMap<Element, Integer>();

		state = new BoardState();

		// awful hack
		if (!(this instanceof SuperZBoard)) {
			elements = new DefaultZWorldCreator().createDefaultElementSet();
			state.enterX = state.enterY = 3;
			setACE(elementAt(3, 3));
		}
	}

	/**
	 * @param game
	 */
	public ZBoard(Element[][][] elements2, String boardName2, int shots2,
			int dark2, int boardNorth2, int boardSouth2, int boardWest2,
			int boardEast2, int restart2, String message2, int enterX2,
			int enterY2, int timeLimit2, int playerX2, int playerY2) {
		this();

		this.elements = elements2;

		state = new BoardState(boardName2, shots2, dark2, boardNorth2,
				boardSouth2, boardWest2, boardEast2, restart2, message2,
				enterX2, enterY2, timeLimit2);

		setACE(elementAt(playerX2, playerY2));

		for (Element iterE : getElementList()) {
			if (iterE instanceof Line)
				((Line) iterE).recalculateLineWalls(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ZZT Board [" + BOARD_WIDTH + "x" + BOARD_HEIGHT + "x"
				+ BOARD_DEPTH + "]\r\n" + state.toString();
	}

	@Override
	public BoardState getState() {
		return state;
	}

	/**
	 * @return
	 */
	public Element[][][] getElements() {
		return elements;
	}

	public Map<Element, Integer> getConveyedElements() {
		return conveyed;
	}

	/**
	 * 
	 */
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

	/**
	 * Returns -1 if in bounds or the direction similar to
	 * <code>OOPHelpers.getDir()</code>
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int boundsCheck(int x, int y) {
		if (x >= getWidth())
			return 1;
		else if (x < 0)
			return 3;
		else if (y >= getHeight())
			return 2;
		else if (y < 0)
			return 0;
		else
			return -1;
	}

	/**
	 * Performs board ticking actions. Should be called as a part of the game
	 * simulation system.
	 */
	public void tick() {
		conveyed.clear();

		if (messageTime > 0) {
			messageTime--;
		}
	}

	/**
	 * This returns the current message on this board - it does not check if a
	 * "menu" (multi line message) is open.
	 * 
	 * <p>
	 * Returns message content or <code>null</code>
	 * 
	 * @return
	 */
	public String getMessage() {
		if (messageTime > 0)
			return state.message;
		else
			return null;
	}

	/**
	 * @param title
	 * @param msg
	 * @param callback
	 * @param rider
	 */
	@Override
	public void setMessage(String msg) {
		messageTime = 50;
		state.message = msg;
	}

	/**
	 * Sets the location that the main Player element is located at. This must
	 * be called each time the player is moved since it provides the information
	 * for {@link #getPlayer()}
	 * 
	 * @param x
	 * @param y
	 * @see #getPlayer()
	 */
	public void setACE(Element p) {
		ace = p;
	}

	/**
	 * Returns the main Player element for this board, or <code>null</code> if:
	 * the program has glitched out and the Player isn't where they're supposed
	 * to be, or no player exists on the board.
	 * <p>
	 * This method may, however, choose to search the board for the first Player
	 * element and return that instead.
	 * 
	 * @return
	 */
	public Player getPlayer() {
		if (!(ace instanceof Player)) {
			List<Element> players = getElementsByType(Player.class);
			if (players.size() > 0) {
				ace = players.get(0);
			}
		}

		return (Player) ace;
		//
		// if (elements[playerX][playerY][0] instanceof Player)
		// return (Player) elements[playerX][playerY][0];
		// else {
		// for (Element e : getElementsByType(Player.class)) {
		// return (Player) e;
		// }
		//
		// return null;
		// }
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
