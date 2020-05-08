/**
 * 
 */
package com.isaacbrodsky.freeze2.game.editor;

/**
 * @author isaac
 * 
 */
public enum EditMode {
	// TODO: Multiselect?
	// TODO: Gradients, like KevEdit?
	SELECT(197), DRAW(220), TYPE(219);

	private final int ch;

	EditMode(int ch) {
		this.ch = ch;
	}

	public int cursorCharacter() {
		return ch;
	}
}
