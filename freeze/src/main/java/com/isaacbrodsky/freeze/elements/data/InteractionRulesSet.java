/**
 * 
 */
package com.isaacbrodsky.freeze.elements.data;

import java.util.HashSet;
import java.util.List;

/**
 * This class exists TODO document
 * 
 * @author isaac
 */
public class InteractionRulesSet extends HashSet<InteractionRule> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8559470380618217578L;

	/**
	 * @param rules
	 */
	public InteractionRulesSet(InteractionRule... rules) {
		super();

		for (int i = 0; i < rules.length; i++)
			add(rules[i]);
	}

	/**
	 * @param rules
	 */
	public InteractionRulesSet(List<InteractionRule> rules) {
		this(rules.toArray(new InteractionRule[rules.size()]));
	}

	/**
	 * Constructs with the empty set
	 */
	public InteractionRulesSet() {
		this(new InteractionRule[0]);
	}

	/**
	 * Wraps {{@link #contains(Object)}
	 * 
	 * @param i
	 * @return
	 * @see #contains(Object)
	 */
	public boolean is(InteractionRule i) {
		return this.contains(i);
	}

}
