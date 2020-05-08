package com.isaacbrodsky.freeze2.elements;

import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.game.Stat;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;

public class Pusher extends ElementImpl {
    @Override
    public GraphicsBlock draw(GameController game, Board board, int x, int y, Tile t, Element e) {
        Stat s = board.statAt(x, y);
        int ch;

        if (s.stepX == 1)
            ch = 16;
        else if (s.stepX == -1)
            ch = 17;
        else if (s.stepY == -1)
            ch = 30;
        else
            ch = 31;

        return new GraphicsBlock(t.getColor(), ch);
    }
}
