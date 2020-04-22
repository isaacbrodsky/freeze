/**
 * 
 */
package com.isaacbrodsky.freeze.ui;

import java.awt.Component;

/**
 * @author isaac
 * 
 */
public interface ZHost {
	public Component getHostComponenet();

	public String getParameter(String n);

	public Component getDialogParent();

	/**
	 * Called when the user requests a quit.
	 */
	public void quit();
	
	public void setTitle(String t);
}
