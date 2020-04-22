/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import java.util.ArrayList;

import com.isaacbrodsky.freeze.EmuMode;
import com.isaacbrodsky.freeze.elements.data.Stats;
import com.isaacbrodsky.freeze.menus.EditorInput;
import com.isaacbrodsky.freeze.menus.TextInput;
import com.isaacbrodsky.freeze.menus.UIInteraction;

/**
 * @author isaac
 *
 */
public final class ElementEditorDefaults {
	private ElementEditorDefaults() {
		
	}

	public static void generateStatUIFields(EmuMode emu, Element e, ArrayList<UIInteraction> ui,
			Stats c) {
		ui.add(new TextInput("Parameter 1:", Integer.toString(c.p1), false));
		ui.add(new TextInput("Parameter 2:", Integer.toString(c.p2), false));
		ui.add(new TextInput("Parameter 3:", Integer.toString(c.p3), false));
		ui.add(new TextInput("Step X:", Integer.toString(c.stepX), false));
		ui.add(new TextInput("Step Y:", Integer.toString(c.stepY), false));
		ui.add(new TextInput("Cycle:", Integer.toString(c.cycle), false));
		ui.add(new TextInput("Current instruction:", Integer
				.toString(c.currInstr), false));
		ui.add(new EditorInput("OOP:", c.oop));
	}
}
