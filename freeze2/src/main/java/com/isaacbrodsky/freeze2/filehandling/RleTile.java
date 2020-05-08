package com.isaacbrodsky.freeze2.filehandling;

import com.isaacbrodsky.freeze2.game.Tile;

public class RleTile {
    public final int repeat;
    public final Tile tile;

    public RleTile(Tile t) {
        this(t, 1);
    }

    public RleTile(Tile t, int repeat) {
        this.repeat = repeat;
        this.tile = t;
    }

    public RleTile inc() {
        return new RleTile(tile, repeat + 1);
    }
}
