/**
 * 
 */
package com.isaacbrodsky.freeze2.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.isaacbrodsky.freeze2.elements.CommonElements;
import com.isaacbrodsky.freeze2.elements.Element;

/**
 */
public class Stat {
	public int x, y;
	public int p1, p2, p3;
	public int stepX, stepY;
	public int cycle;
	public int follower, leader;
	public Tile under;
	public int currInstr;
	/**
	 * If this number is negative, it refers to a different object's OOP, and
	 * that this object has no OOP of it's own. The OOP of the refrenced element
	 * can be retrieved by {@link #oop}, implementations which create Stats
	 * objects should take care to fill the oop field correctly to support this.
	 *
	 * <p>
	 * To obtain the actual length of this Stat elements OOP data, use
	 * <code>.getOop().length()</code>
	 */
	public int oopLength;
	public String oop;

	public Stat(int x, int y) {
		this(x, y, 0, 0, 0, 0, 0, 3, 0, new Tile(CommonElements.EMPTY, 0));
	}

	public Stat(int statX, int statY, int p1, int p2, int p3, int stepX,
                int stepY, int cycle, int currInstr, Tile under) {
		this(statX, statY, p1, p2, p3, stepX, stepY, cycle, -1, -1, currInstr, under,
				0, "");
	}

	public Stat(Stat s) {
		assignFrom(s);
	}

	@JsonCreator
	public Stat(
			@JsonProperty("x") int statX,
			@JsonProperty("y") int statY,
			@JsonProperty("p1") int p1,
			@JsonProperty("p2") int p2,
			@JsonProperty("p3") int p3,
			@JsonProperty("stepX") int stepX,
			@JsonProperty("stepY") int stepY,
			@JsonProperty("cycle") int cycle,
			@JsonProperty("follower") int follower,
			@JsonProperty("leader") int leader,
			@JsonProperty("currInstr") int currInstr,
			@JsonProperty("under") Tile under,
			@JsonProperty("oopLength") int oopLength,
			@JsonProperty("oop") String oop
	) {
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
		this.under = under;
		this.currInstr = currInstr;
		this.oopLength = oopLength;
		this.oop = oop;
	}

	public void assignFrom(Stat s) {
		this.x = s.x;
		this.y = s.y;
		this.p1 = s.p1;
		this.p2 = s.p2;
		this.p3 = s.p3;
		this.stepX = s.stepX;
		this.stepY = s.stepY;
		this.cycle = s.cycle;
		this.follower = s.follower;
		this.leader = s.leader;
		this.under = s.under;
		this.currInstr = s.currInstr;
		this.oopLength = s.oopLength;
		this.oop = s.oop;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("X/Y: " + x + "/" + y);
		sb.append("\nP1/2/3: " + p1 + "/" + p2 + "/" + p3);
		sb.append("\nStep X/Y: " + stepX + "/" + stepY);
		sb.append("\nCycle: " + cycle);
		sb.append("\nFollower/leader: " + follower + "/" + leader);
		sb.append("\nUnder: " + under);
		sb.append("\nCurr instr/OOP length: " + currInstr + "/" + oopLength);
		sb.append("\nOOP:\r\n" + oop + "\n");
		return sb.toString();
	}

	@Override
	public Stat clone() {
		return new Stat(this);
	}
}
