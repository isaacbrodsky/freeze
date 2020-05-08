package com.isaacbrodsky.freeze2.elements;

import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.game.Stat;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;

public class Duplicator extends ElementImpl {
    private static final int[] DUPLICATOR_CHARS = {
            250, 250, 249, 248, 111, 79
    };

    @Override
    public GraphicsBlock draw(GameController game, Board board, int x, int y, Tile t, Element e) {
        Stat stat = board.statAt(x, y);
        int ch = DUPLICATOR_CHARS[0];
        if (stat.p1 > 0 && stat.p1 < DUPLICATOR_CHARS.length) {
            ch = DUPLICATOR_CHARS[stat.p1];
        }
        return new GraphicsBlock(t.getColor(), ch);
    }
}
