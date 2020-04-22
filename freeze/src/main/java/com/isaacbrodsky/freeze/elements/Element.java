/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import com.isaacbrodsky.freeze.elements.data.*;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.graphics.ElementColoring;

/**
 * @author isaac
 * 
 */
public interface Element {
	/**
	 * 
	 * 
	 * @param type
	 * @param color
	 */
	public void createInstance(SaveData dat);

	public SaveData getSaveData();

	public Stats getStats();

	public void setStats(Stats stats);

	/**
	 * Retrieves this elements interaction rules set.
	 * 
	 * <p>
	 * See {@link #tick(GameController, Board)} for limitations on this method.
	 * 
	 * @return
	 */
	public InteractionRulesSet getInteractionsRules();

	/**
	 * Notifies this element of a message. Messages are commonly used to notify
	 * an element of being touched by the player, shot, bombed, certain world
	 * and board-level events (such as energized, and in SuperZZT the H key
	 * being pressed and also board entering.)
	 * 
	 * <p>
	 * Whether or not the message is one of these built-ins can be tested with
	 * the appropriate methods in {@link Message}. Another element may have
	 * constructed a message that is equivalent to a built-in one, so there is
	 * no guarantee as to the origin of a message. The message should be handled
	 * solely based on contents alone, determining the origin is not possible.
	 * 
	 * @param game
	 * @param board
	 * @param msg
	 */
	public void message(GameController game, Board board, Message msg);

	/**
	 * Get this element's cycle. A higher cycle will result in the element's
	 * <code>tick</code> method being called less often. A cycle of
	 * <code>0</code> indicates this element does not need or want it's
	 * <code>tick</code> method to be called. A cycle less than <code>0</code>
	 * is illegal.
	 * 
	 * <p>
	 * See {@link #tick(GameController, Board)} for limitations on this method.
	 * 
	 * @return
	 */
	public int getCycle();

	/**
	 * Perform any per-cycle tasks. This element should not perform it's own
	 * cycle adjustments to it's timing, this is done by the controller.
	 * 
	 * <p>
	 * The element should not use {@link GameController#getBoard()}, but should
	 * use the board passed to it instead.
	 * 
	 * <h3>Limitations</h3>
	 * <p>
	 * An element may only change it's cycle, interaction rules set, display
	 * character and coloring during this call. It may not rely on when the
	 * {@link GameController} does and does not call these data retrieval
	 * methods.
	 */
	public void tick(GameController game, Board board);

	/**
	 * Get the character to display this element as.
	 * 
	 * <p>
	 * See {@link #tick(GameController, Board)} for limitations on this method.
	 * 
	 * @return A character index, 0&lt;=x&lt;=255
	 */
	public int getDisplayCharacter();

	/**
	 * Get the coloring rules to the display this element with.
	 * 
	 * <p>
	 * See {@link #tick(GameController, Board)} for limitations on this method.
	 * 
	 * @return
	 */
	public ElementColoring getColoring();

	/**
	 * Let's this element know it's location.
	 * 
	 * @param x
	 * @param y
	 */
	public void setXY(int x, int y);

	/**
	 * @return
	 */
	public int getX();

	/**
	 * @return
	 */
	public int getY();
}
