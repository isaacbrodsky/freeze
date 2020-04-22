/**
 * 
 */
package com.isaacbrodsky.freeze.game;

import java.util.ArrayList;

import com.isaacbrodsky.freeze.elements.ElementDefaults;
import com.isaacbrodsky.freeze.elements.ElementResolver;
import com.isaacbrodsky.freeze.elements.Player;
import com.isaacbrodsky.freeze.graphics.Renderer;
import com.isaacbrodsky.freeze.menus.MenuCallback;
import com.isaacbrodsky.freeze.menus.UIInteraction;

/**
 * @author isaac
 * 
 */
public interface GameController {

	/**
	 * By ref.
	 * 
	 * @return
	 */
	public GameState getState();

	/**
	 * @param boards
	 */
	public void setBoardList(ArrayList<Board> boards);

	/**
	 */
	public ArrayList<Board> getBoardList();

	/**
	 * Renders this GameController onto the given Renderer.
	 * 
	 * @param renderer
	 * @param blinking
	 * @param flashingTick
	 */
	public void render(Renderer renderer, boolean blinking);

	/**
	 * Either sets the menu to one created using the provided data or sets the
	 * message on the current board.
	 * 
	 * @param title
	 * @param msg
	 * @param callback
	 * @param rider
	 */
	public void setMenu(String title, String msg, MenuCallback callback,
			Object rider);

	/**
	 * @param i
	 */
	public void setBoard(int i);

	/**
	 * Returns the current board object.
	 * 
	 * @return
	 */
	public Board getBoard();

	/**
	 * @return
	 */
	public int getBoardIdx();


	public UIInteraction getMenu();

	/**
	 * @param elapsed
	 */
	public void runSimulation(long elapsed);

	/**
	 * The board may have more than one Player element, this function returns
	 * ONLY the first. (or Active)
	 * 
	 * @return
	 */
	public Player getPlayer();

	public void startPlaying();

	/**
	 * @return
	 */
	public boolean isPaused();

	public void setPaused(boolean paused);

	public void reportError(String msg);

	/**
	 * @param msg
	 */
	void setMessage(String msg);

	/**
	 * @param title
	 * @param msg
	 * @param callback
	 * @param rider
	 */
	void setMessage(String title, String msg, MenuCallback callback,
			Object rider);

	public ElementResolver getElementResolver();

	public ElementDefaults getElementDefaults();
}
