/**
 * 
 */
package com.isaacbrodsky.freeze.elements.data;

/**
 * @author isaac
 */
public enum InteractionRule {
	/**
	 * This element can be pushed (only North/South, if <code>MOVEABLE_EW</code>
	 * is set as well than it can be moved in any direction)
	 */
	MOVEABLE_NS,
	/**
	 * This element can be pushed (only East/West, if <code>MOVEABLE_NS</code>
	 * is set as well than it can be moved in any direction)
	 */
	MOVEABLE_EW,
	/**
	 * Bullets and stars (but not players or other elements) can move over this
	 * element.
	 * 
	 * <p>
	 * Water is an example of this rule.
	 */
	SHOOTOVER,
	/**
	 * This element can be shot at point blank. This denotes a "breakable"
	 * element, such as breakables, gems, and built-in enemies.
	 */
	POINT_BLANK_SHOOTABLE,
	/**
	 * All non floor elements can move over this element.
	 */
	FLOOR,
	/**
	 * 
	 */
	ITEM,
	/**
	 * 
	 */
	BLOCKS_MOVE,
	/**
	 * Cannot be moved by a Player
	 */
	NOT_PLAYER_MOVEABLE,
	/**
	 * Element can only be shot by the player
	 */
	ONLY_PLAYER_SHOOTABLE,
	/**
	 * Element cannot go through a <code>Transporter</code>
	 */
	NO_TRANSPORTER
}