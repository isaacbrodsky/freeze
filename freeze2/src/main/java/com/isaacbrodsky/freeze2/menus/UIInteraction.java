/**
 * 
 */
package com.isaacbrodsky.freeze2.menus;

import com.isaacbrodsky.freeze2.graphics.Renderer;

/**
 * @author isaac
 * 
 */
public interface UIInteraction {
	// TODO: Doesn't support modifiers
	boolean keyPress(int key);

	String getSelectedText();

	String getSelectedLabel();

	/**
	 * Renders this interaction.
	 * 
	 * @param renderer
	 * @param yoff
	 *            Y offset this interaction should be drawn at. Note that some
	 *            interactions have their own controls on where they will draw,
	 *            interactions must combine their own y positioning and this
	 *            number.
	 * @param focused
	 *            false if this interaction is being drawn but will not recieve
	 *            user input; this is provided so an interaction can choose to
	 *            not draw carrots, etc
	 */
	void render(Renderer renderer, int yoff, boolean focused);

	/**
	 */
	void tick();

	/**
	 * Returns true if this input is still accepting input and should be kept.
	 * Note that some inputs never return false on this method and should
	 * instead have their result queried via {@link #getSelectedText()} at the
	 * controller's convenience.
	 * 
	 * <p>
	 * If an interaction is being disposed of after a call to this method,
	 * {@link #tick()} must be called before that disposal happens so that
	 * callbacks will be called.
	 * 
	 * @return
	 */
	boolean stillAlive();

	/**
	 * Some UIInteractions open "subinteractions", dialogs within dialogs,
	 * inputs within inputs, menus within menus, etc. This method will either
	 * return the object it was called on or that object's currently foreground
	 * interaction.
	 * 
	 * @return
	 */
	UIInteraction getFocusedInteraction();
}
