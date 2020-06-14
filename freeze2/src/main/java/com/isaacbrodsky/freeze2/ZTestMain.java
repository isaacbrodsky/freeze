/**
 * 
 */
package com.isaacbrodsky.freeze2;

import com.isaacbrodsky.freeze2.graphics.*;
import com.isaacbrodsky.freeze2.menus.CharInput;
import com.isaacbrodsky.freeze2.menus.UIInteraction;
import com.isaacbrodsky.freeze2.utils.TimeAndMathUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Provides test and utility methods
 * 
 * @author isaac
 * 
 */
public class ZTestMain {
	public static void renderTestPattern(Renderer r) {
		Random rand = new Random();
		int c = rand.nextInt(256), ca = 15, cb = 0;
		for (int y = 0; y < 25; y++) {
			for (int x = 0; x < 60; x++) {
				r.set(x, y, new GraphicsBlock(ElementColoring.forNames(
						NamedColor.colorFromCode(ca), NamedColor
								.colorFromCode(cb)), c));
				c++;
				ca--;
				if (ca == 0) {
					cb++;
					ca = 15;
				}
				if (cb == 16)
					cb = 0;
				if (c == 256)
					c = 0;
			}
		}
	}

	public static void renderSidebarPattern(Renderer r) {
		Random rand = new Random();
		int c = rand.nextInt(256), ca = 15, cb = rand.nextInt(15);
		for (int y = 0; y < Sidebar.SIDEBAR_HEIGHT; y++) {
			for (int x = Sidebar.SIDEBAR_OFFSET; x < Sidebar.SIDEBAR_OFFSET
					+ Sidebar.SIDEBAR_WIDTH; x++) {
				r.set(x, y, new GraphicsBlock(ElementColoring.forNames(
						NamedColor.colorFromCode(ca), NamedColor
								.colorFromCode(cb)), c));
				c++;
				ca--;
				if (ca == 0) {
					cb++;
					ca = 15;
				}
				if (cb == 16)
					cb = 0;
				if (c == 256)
					c = 0;
			}
		}
	}

	public static UIInteraction generateASCIITable(boolean useHex) {
		final int CHAR_REF_HEIGHT = 22;

		StringBuilder sb = new StringBuilder();
		StringBuilder cur;

		ArrayList<StringBuilder> list = new ArrayList<StringBuilder>(
				CHAR_REF_HEIGHT);
		for (int i = 0; i < CHAR_REF_HEIGHT; i++)
			list.add(new StringBuilder());

		int y = 0;
		for (int i = 0; i < 256; i++) {
			cur = list.get(y);

			if (cur.length() != 0)
				cur = cur.append(" ");
			cur = cur.append((char) (i == 10 ? ' ' : i));
			cur = cur.append(TimeAndMathUtils.padInt(i, (useHex ? 2 : 3), '0',
					(useHex ? 16 : 10)));

			y++;
			if (y == CHAR_REF_HEIGHT) {
				y = 0;
			}
		}

		for (int i = 0; i < list.size(); i++) {
			cur = list.get(i);
			if (cur == null)
				continue;
			sb.append(cur).append('\n');
		}

		return new CharInput("Character Reference" + (useHex ? " (Hex)" : "")
				+ "\n" + sb.toString() + "Any key to continue", null, true,
				null, 0, false);
	}

}
