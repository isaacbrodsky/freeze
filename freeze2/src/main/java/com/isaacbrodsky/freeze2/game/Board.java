/**
 * 
 */
package com.isaacbrodsky.freeze2.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author isaac
 * 
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
		@JsonSubTypes.Type(ZBoard.class),
		@JsonSubTypes.Type(SuperZBoard.class)
})
public interface Board {
	@JsonIgnore
	int getWidth();
	@JsonIgnore
	int getHeight();

	@JsonProperty("tiles")
	Tile[][] getTiles();
	Tile tileAt(int x, int y);
	boolean inBounds(int x, int y);
	Stream<Tile> tileStream();
	void putTileAndStats(int x, int y, Tile t, Stat... stats);

	@JsonProperty("stats")
	List<Stat> getStats();
	/**
	 * First stat pointing to this location
	 */
	Stat statAt(int x, int y);
	/**
	 * First stat pointing to this location
	 */
	int statIdAt(int x, int y);
	@JsonIgnore
	Stat getPlayer();
	void moveStat(int idx, int x, int y);

	/**
	 * Performs board ticking actions. Should be called as a part of the game
	 * simulation system.
	 */
	void tick();

	/**
	 * @return
	 */
	@JsonProperty("state")
	BoardState getState();

	Board copyBoard();
}
