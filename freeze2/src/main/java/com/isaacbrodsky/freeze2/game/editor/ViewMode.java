/**
 * 
 */
package com.isaacbrodsky.freeze2.game.editor;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author isaac
 * 
 */
public enum ViewMode {
	EDITOR,
	PREVIEW,
	EMPTIES,
	STAT_COUNT,
	STAT_ORDER,
	SUPER_WINDOW,
	MONOCHROME,
	OOP_PRESENT,
	WALKABLE;

	public static String makeViewModeList() {
		return AddElementUtils.makeSelectionList(
				Stream.of(values())
				.map(m -> new AddElementUtils.Selection(Integer.toString(m.ordinal() + 1), m.name()))
				.collect(Collectors.toList()),
				true);
	}

	public static ViewMode forCode(String cmd) {
		int i = cmd.charAt(0) - '1';
		for (ViewMode m : values()) {
			if (m.ordinal() == i) {
				return m;
			}
		}
		return null;
	}
}
