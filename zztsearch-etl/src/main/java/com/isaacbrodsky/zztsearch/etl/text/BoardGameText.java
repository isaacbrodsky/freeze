package com.isaacbrodsky.zztsearch.etl.text;

import lombok.Value;

@Value
public class BoardGameText implements GameText {
    /**
     * World containing this board
     */
    private WorldGameText world;

    /**
     * Title of the board
     */
    private String title;

    /**
     * Message currently being flashed in game
     */
    private String message;

    /**
     * Which number the board is.
     */
    private int index;

    /**
     * Hash of the contents of the board
     */
    private String visualHash;

    public boolean isTitle() {
        return index == 0;
    }

    @Override
    public void accept(GameTextVisitor visitor) {
        visitor.visit(this);
    }
}
