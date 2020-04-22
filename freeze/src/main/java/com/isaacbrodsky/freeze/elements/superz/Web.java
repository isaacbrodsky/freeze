/**
 * 
 */
package com.isaacbrodsky.freeze.elements.superz;

import com.isaacbrodsky.freeze.elements.Line;
import com.isaacbrodsky.freeze.elements.data.InteractionRule;
import com.isaacbrodsky.freeze.elements.data.InteractionRulesSet;

/**
 * @author isaac
 * 
 */
public class Web extends Line {
	private static final InteractionRulesSet _IRS = new InteractionRulesSet(
			InteractionRule.FLOOR);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getInteractionsRules()
	 */
	@Override
	public InteractionRulesSet getInteractionsRules() {
		return _IRS;
	}

	@Override
	public int getDisplayCharacter() {
		switch (getDir()) {
		case 0:
			return 46;
		case 1:
			return 196;
		case 2:
			return 196;
		case 3:
			return 196;
		case 4:
			return 179;
		case 5:
			return 217;
		case 6:
			return 192;
		case 7:
			return 193;
		case 8:
			return 179;
		case 9:
			return 191;
		case 10:
			return 218;
		case 11:
			return 194;
		case 12:
			return 179;
		case 13:
			return 180;
		case 14:
			return 195;
		case 15:
		}
		return 197;
	}
}
