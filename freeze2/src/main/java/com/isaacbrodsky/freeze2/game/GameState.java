/**
 * 
 */
package com.isaacbrodsky.freeze2.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.isaacbrodsky.freeze2.EmuMode;

/**
 * Having all these fields as <code>public</code> is probably stupid but by
 * george it's easier.
 * 
 * @author isaac
 */
public class GameState {
	public int boards;
	public int ammo;
	public int gems;
	public int[] keys;
	public int health;
	/**
	 * The index of the start board (the board the game sets to after P is
	 * pressed on the title screen.) This is allowed to be 0 to indicate the
	 * title screen.
	 */
	public int startBoard;
	public int torches;// ZZT only
	public int tcycles;// ZZT only
	public int ecycles;
	public int score;
	public String gameName;
	public String[] flags;
	public int timePassed;
	public int isSave;
	public int stones;// SZZT only

	public GameState(EmuMode mode) {
		this.boards = 0;
		this.ammo = 0;
		this.gems = 0;
		this.keys = new int[] { 0, 0, 0, 0, 0, 0 };
		this.health = 100;
		this.startBoard = 0;
		this.torches = 0;
		this.tcycles = 0;
		this.ecycles = 0;
		this.score = 0;
		this.gameName = "";
		this.flags = new String[10];
		if (mode.equals(EmuMode.SUPERZZT)) {
			flags = new String[16];
		}
		for (int i = 0; i < flags.length; i++) {
			flags[i] = null;
		}
		this.timePassed = 0;
		this.isSave = 0;
		this.stones = -1;
	}

	@JsonCreator
	public GameState(
			@JsonProperty("boards") int boards,
			@JsonProperty("ammo") int ammo,
			@JsonProperty("gems") int gems,
			@JsonProperty("keys") int[] keys,
			@JsonProperty("health") int health,
			@JsonProperty("startBoard") int startBoard,
			@JsonProperty("torches") int torches,
			@JsonProperty("tcycles") int tcycles,
			@JsonProperty("ecycles") int ecycles,
			@JsonProperty("score") int score,
			@JsonProperty("gameName") String gameName,
			@JsonProperty("flags") String[] flags,
			@JsonProperty("timePassed") int timePassed,
			@JsonProperty("isSave") int isSave,
			@JsonProperty("stones") int stones
	) {
		this.boards = boards;
		this.ammo = ammo;
		this.gems = gems;
		this.keys = keys;
		this.health = health;
		this.startBoard = startBoard;
		this.torches = torches;
		this.tcycles = tcycles;
		this.ecycles = ecycles;
		this.score = score;
		this.gameName = gameName;
		this.flags = flags;
		this.timePassed = timePassed;
		this.isSave = isSave;
		this.stones = stones;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Boards: " + boards + "\r\n");
		sb.append("Ammo: " + ammo + "\r\n");
		sb.append("Gems: " + gems + "\r\n");
		sb.append("Health: " + health + "\r\n");
		sb.append("Start Board: " + startBoard + "\r\n");
		sb.append("Torches: " + torches + "\r\n");
		sb.append("Tcycles: " + tcycles + "\r\n");
		sb.append("Ecycles: " + ecycles + "\r\n");
		sb.append("Score: " + score + "\r\n");
		sb.append("Game Name: " + gameName + "\r\n");
		sb.append("Flags\r\n");
		for (int i = 0; i < flags.length; i++) {
			sb.append("\tFlag " + i + ": " + flags[i] + "\r\n");
		}
		for (int i = 0; i < keys.length; i++) {
			sb.append("\tKey " + i + ": " + keys[i] + "\r\n");
		}
		sb.append("Time Passed: " + timePassed + "\r\n");
		sb.append("Save: " + isSave + "\r\n");
		sb.append("Stones: " + stones + "\r\n");

		return sb.toString();
	}
}
