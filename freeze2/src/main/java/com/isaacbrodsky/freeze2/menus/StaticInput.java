/**
 * 
 */
package com.isaacbrodsky.freeze2.menus;

import com.isaacbrodsky.freeze2.graphics.Renderer;

/**
 * @author isaac
 * 
 */
public class StaticInput<T> extends MenuUtils implements UIInteraction {
	private final String title;
	private final T rider;

	public StaticInput(String title) {
		this(title, null);
	}

	public StaticInput(String title, T rider) {
		this.title = title;
		this.rider = rider;
	}

	@Override
	public boolean keyPress(int key) {
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
		renderInputMessage(renderer, yoff, title, false);
	}

	@Override
	public void tick() {
	}

	@Override
	public boolean stillAlive() {
		return true;
	}
	
	@Override
	public UIInteraction getFocusedInteraction() {
		return this;
	}

	public T getRider() {
		return rider;
	}
}
