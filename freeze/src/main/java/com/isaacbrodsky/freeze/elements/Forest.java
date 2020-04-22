/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import com.isaacbrodsky.freeze.elements.data.InteractionRule;
import com.isaacbrodsky.freeze.elements.data.InteractionRulesSet;

/**
 * @author isaac
 * 
 */
public class Forest extends Solid {
	private static final InteractionRulesSet _IRS = new InteractionRulesSet(
			InteractionRule.ITEM);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getInteractionsRules()
	 */
	@Override
	public InteractionRulesSet getInteractionsRules() {
		return _IRS;
	}

	public int getDisplayCharacter() {
		return 176;
	}
}
