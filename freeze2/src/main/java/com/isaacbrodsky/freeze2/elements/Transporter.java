package com.isaacbrodsky.freeze2.elements;

import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.game.Stat;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;

public class Transporter extends ElementImpl {
    private static final int[] NS_CHARS = {'^', '~', '^', '-', 'v', '_', 'v', '-'};
    private static final int[] EW_CHARS = {'(', '<', '(', 179, ')', '>', ')', 179};

    @Override
    public GraphicsBlock draw(GameController game, Board board, int x, int y, Tile t, Element e) {
        Stat stat = board.statAt(x, y);

        int ch;
        if (stat.stepX == 0) {
            ch = NS_CHARS[stat.stepY * 2 + 3 + (game.currentTick() / stat.cycle) % 4];
        } else {
            ch = EW_CHARS[stat.stepX * 2 + 3 + (game.currentTick() / stat.cycle) % 4];
        }

        return new GraphicsBlock(t.getColor(), ch);
    }
}
