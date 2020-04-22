/**
 * 
 */
package com.isaacbrodsky.freeze.menus;

/**
 * @author isaac
 *
 */
public class MenuItem {
	ItemType type;
	String label,data;
	
	public MenuItem(String d) {
		this(d, ItemType.NORMAL, null);
	}
	
	public MenuItem(String d, ItemType t) {
		this(d, t, null);
	}
	
	public MenuItem(String d, ItemType t, String l) {
		this.data = d;
		this.type = t;
		this.label = l;
	}
	
	public static enum ItemType {
		NORMAL, CENTERED, SELECTABLE
	}
}
