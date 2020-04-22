/**
 * 
 */
package com.isaacbrodsky.freeze.elements.data;

import com.isaacbrodsky.freeze.elements.Element;

/**
 * Probably <s>in</s>appropriate use of public fields. Data dump class.
 * 
 * <p>
 * Because this is a mutable class, you must ensure that the same Stats object
 * is not assigned to more than one Element at a time (unless you wish the
 * elements to be synchronized - but keep in mind that if one element modifies
 * the Stats during its cycle, the next element will see the modified stats)
 * 
 * @author isaac
 * 
 */
public class Stats implements Cloneable {
	// public static final Stats BLANK_STATS = new Stats(0, 0, 0, 0, 0, 0, 0, 0,
	// 0, null, null, null);
//	public static final Stats BLANK_STATS = new Stats();
	
	//doesn't appear to be meaningful
	// public static final Stats DEFAULT_PLAYER_STATS = new Stats(0, 0, 30, 18,
	// 1, 25088, 1105, 1, 30720, 544, 0, 0, "");

	public int x, y;
	public int p1, p2, p3;
	public int stepX, stepY;
	public int cycle;
	public int follower, leader;
	public int currInstr, oopLength;
	public String oop;

	public Element elFollower, elLeader;
	public Stats statsOop;

	public Stats() {
		this(0, 0, 0, 0, 0, 0, 0, 3, 0, null, null, null);
	}

	public Stats(int statX, int statY, int p1, int p2, int p3, int stepX,
			int stepY, int cycle, int currInstr, Element fol, Element lead,
			Stats oop) {
		this(statX, statY, p1, p2, p3, stepX, stepY, cycle, -1, -1, currInstr,
				0, "");

		this.elFollower = fol;
		this.elLeader = lead;
		this.statsOop = oop;
	}

	public Stats(int statX, int statY, int p1, int p2, int p3, int stepX,
			int stepY, int cycle, int currInstr, String oop) {
		this(statX, statY, p1, p2, p3, stepX, stepY, cycle, -1, -1, currInstr,
				oop.length(), oop);
	}

	public Stats(Stats s) {
		this.x = s.getX();
		this.y = s.getY();
		this.p1 = s.getP1();
		this.p2 = s.getP2();
		this.p3 = s.getP3();
		this.stepX = s.getStepX();
		this.stepY = s.getStepY();
		this.cycle = s.getCycle();
		this.follower = s.getFollower();
		this.leader = s.getLeader();
		this.currInstr = s.getCurrInstr();
		this.oopLength = s.getOopLength();
		this.oop = s.getOop();

		this.elFollower = s.getFollowerElement();
		this.elLeader = s.getLeaderElement();
		this.statsOop = s.getOopStats();
	}

	public Stats(int statX, int statY, int p1, int p2, int p3, int stepX,
			int stepY, int cycle, int follower, int leader, int currInstr,
			int oopLength, String oop) {
		this.x = statX;
		this.y = statY;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.stepX = stepX;
		this.stepY = stepY;
		this.cycle = cycle;
		this.follower = follower;
		this.leader = leader;
		this.currInstr = currInstr;
		this.oopLength = oopLength;
		this.oop = oop;

		this.elFollower = null;
		this.elLeader = null;
		this.statsOop = null;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	/**
	 * @return the p1
	 */
	public int getP1() {
		return p1;
	}

	/**
	 * @return the p2
	 */
	public int getP2() {
		return p2;
	}

	/**
	 * @return the p3
	 */
	public int getP3() {
		return p3;
	}

	/**
	 * @return the stepX
	 */
	public int getStepX() {
		return stepX;
	}

	/**
	 * @return the stepY
	 */
	public int getStepY() {
		return stepY;
	}

	/**
	 * @return the cycle
	 */
	public int getCycle() {
		return cycle;
	}

	/**
	 * @return the follower
	 */
	public int getFollower() {
		return follower;
	}

	/**
	 * @return the leader
	 */
	public int getLeader() {
		return leader;
	}

	/**
	 * @return the currInstr
	 */
	public int getCurrInstr() {
		return currInstr;
	}

	/**
	 * If this number is negative, it refers to a different object's OOP, and
	 * that this object has no OOP of it's own. The OOP of the refrenced element
	 * can be retrieved by {@link #getOop()}, implementations which create Stats
	 * objects should take care to fill the oop field correctly to support this.
	 * 
	 * <p>
	 * To obtain the actual length of this Stat elements OOP data, use
	 * <code>.getOop().length()</code>
	 * 
	 * @return
	 */
	public int getOopLength() {
		return oopLength;
	}

	/**
	 * Returns a zero-length String if this Stats element has no OOP code
	 * associated with it.
	 * 
	 * @return the oop
	 */
	public String getOop() {
		return oop;
	}

	public Element getFollowerElement() {
		return elFollower;
	}

	public Element getLeaderElement() {
		return elLeader;
	}

	public Stats getOopStats() {
		return statsOop;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("X/Y: " + x + "/" + y);
		sb.append("\nP1/2/3: " + p1 + "/" + p2 + "/" + p3);
		sb.append("\nStep X/Y: " + stepX + "/" + stepY);
		sb.append("\nCycle: " + cycle);
		sb.append("\nFollower/leader: " + follower + "/" + leader);
		if (elFollower != null) {
			sb.append("\nF/l: ").append(elFollower.getX()).append("/").append(
					elFollower.getY()).append(" ").append(elLeader.getX())
					.append("/").append(elLeader.getY());
		}
		sb.append("\nCurr instr/OOP length: " + currInstr + "/" + oopLength);
		sb.append("\nOOP:\r\n" + oop + "\n");
		return sb.toString();
	}

	@Override
	public Stats clone() {
		return new Stats(this);
	}
}
