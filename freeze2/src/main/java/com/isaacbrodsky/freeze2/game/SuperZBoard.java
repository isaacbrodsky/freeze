/**
 * 
 */
package com.isaacbrodsky.freeze2.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author isaac
 * 
 */
@JsonTypeName("SUPERZZT")
public class SuperZBoard extends ZBoard {
	/**
	 * Width in elements of this board.
	 */
	public final static int BOARD_WIDTH = 96;
	/**
	 * Height in elements of this board.
	 */
	public final static int BOARD_HEIGHT = 80;

	@JsonCreator
	public SuperZBoard(
			@JsonProperty("state") BoardState state,
			@JsonProperty("tiles") Tile[][] tiles,
			@JsonProperty("stats") List<Stat> stats
	) {
		super(state, tiles, stats);
	}

	@Override
	public int getWidth() {
		return BOARD_WIDTH;
	}

	@Override
	public int getHeight() {
		return BOARD_HEIGHT;
	}

	@Override
	public Board copyBoard() {
		List<Stat> statsCopy = getStats().stream().map(Stat::clone).collect(Collectors.toList());

		return new SuperZBoard(
				new BoardState(getState()),
				copyTileArray(),
				statsCopy
		);
	}
}
