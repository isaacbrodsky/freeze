/**
 * 
 */
package com.isaacbrodsky.freeze2.menus;

import java.awt.event.KeyEvent;

import com.isaacbrodsky.freeze2.graphics.Renderer;

/**
 * @author isaac
 * 
 */
public class CancelableInput<T> extends MenuUtils implements UIInteraction {
	private boolean stillAlive;

	private String title;
	private T rider;
	private MenuCallback<T> callback;

	private int y;

	public CancelableInput(String title, MenuCallback<T> callback, T rider) {
		this(title, callback, rider, Renderer.DISPLAY_HEIGHT - 2);
	}

	public CancelableInput(String title, MenuCallback<T> callback, T rider,
			int y) {
		this.stillAlive = true;
		this.title = title;
		this.callback = callback;
		this.rider = rider;

		this.y = y;
	}

	@Override
	public boolean keyPress(int key) {
		if (key == KeyEvent.VK_ESCAPE) {
			stillAlive = false;
			return true;
		}
		return false;
	}

	@Override
	public String getSelectedText() {
		return null;
	}

	@Override
	public String getSelectedLabel() {
		return null;
	}

	@Override
	public void render(Renderer renderer, int yoff, boolean focused) {
		renderInputMessage(renderer, y + yoff, title
				+ " (\001ESC\002 to cancel)", focused);
	}

	@Override
	public void tick() {
		if (!stillAlive) {
			if (callback != null) {
				callback.menuCommand(null, rider);
			}
		}
	}

	@Override
	public boolean stillAlive() {
		return stillAlive;
	}

	@Override
	public UIInteraction getFocusedInteraction() {
		return this;
	}
}
