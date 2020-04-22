/**
 * 
 */
package com.isaacbrodsky.freeze.game;

import java.util.List;
import java.util.Map;

import com.isaacbrodsky.freeze.elements.Element;
import com.isaacbrodsky.freeze.elements.Player;

/**
 * @author isaac
 * 
 */
public interface Board {
	/**
	 * @return
	 */
	public int getWidth();

	/**
	 * @return
	 */
	public int getHeight();

	/**
	 * @return
	 */
	public int getDepth();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString();

	/**
	 * @return
	 */
	Element[][][] getElements();

	/**
	 * @return
	 */
	public Map<Element, Integer> getConveyedElements();

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public Element floorAt(int x, int y);

	/**
	 * @param x
	 * @param y
	 * @param depth
	 * @return Non-floor element at the specified location and depth or
	 *         <code>null</code>
	 */
	public Element elementAt(int x, int y, int d);

	/**
	 * @param x
	 * @param y
	 * @return First non-floor element at the specified location or
	 *         <code>null</code>
	 */
	public Element elementAt(int x, int y);

	/**
	 * Calls <code>removeAt(int, int, Element)</code> with <code>null</code> as
	 * the third parameter.
	 * 
	 * @see #removeAt(int, int, Element)
	 * @param x
	 * @param y
	 */
	public void removeAt(int x, int y);

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
	public void removeAt(int x, int y, Element e);

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
	public Element putElement(int x, int y, Element e);

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
	public List<Element> getElementsByType(Class clazz);

	/**
	 * Returns a list of all elements on the board.
	 * 
	 * <p>
	 * Performance: Iterates over all elements on the board.
	 * 
	 * @return
	 */
	public List<Element> getElementList();

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
	public List<Element> getElementsByName(String name);

	/**
	 * Returns -1 if in bounds or the direction similar to
	 * <code>OOPHelpers.getDir()</code>
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int boundsCheck(int x, int y);

	/**
	 * Performs board ticking actions. Should be called as a part of the game
	 * simulation system.
	 */
	public void tick();

	/**
	 * This returns the current message on this board - it does not check if a
	 * "menu" (multi line message) is open.
	 * 
	 * <p>
	 * Returns message content or <code>null</code>
	 * 
	 * @return
	 */
	public String getMessage();

	/**
	 * Sets the current message on this board. This method must only be used for
	 * messages which can be displayed at the bottom of the board; messages that
	 * require more lines or user input should use the message/menu system
	 * provided by the game controller.
	 * 
	 * @param msg
	 */
	public void setMessage(String msg);

	/**
	 * Sets the location that the main Player element is located at. This must
	 * be called each time the player is moved since it provides the information
	 * for {@link #getPlayer()}
	 * 
	 * @param x
	 * @param y
	 * @see #getPlayer()
	 */
	public void setACE(Element p);

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
	public Player getPlayer();

	/**
	 * @return
	 */
	public BoardState getState();
}
