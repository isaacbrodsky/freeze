/**
 * 
 */
package com.isaacbrodsky.freeze2.menus;

/**
 * @author isaac
 *
 */
@FunctionalInterface
public interface MenuCallback<T> {
	void menuCommand(String cmd, T rider);
}
