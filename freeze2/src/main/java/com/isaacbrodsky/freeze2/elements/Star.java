package com.isaacbrodsky.freeze2.elements;

import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.game.Stat;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;

public class Star extends ElementImpl {
    private static final int[] STAR_CHARS = {179, '/', 196, '\\'};

    @Override
    public GraphicsBlock draw(GameController game, Board board, int x, int y, Tile t, Element e) {
        // TODO: Reconstruction of ZZT says the star has a draw proc which modifies
        // it's tile state - not used here (used for modifying the color)
        int ch = STAR_CHARS[game.currentTick() % 4];

        return new GraphicsBlock(t.getColor(), ch);
    }
}
