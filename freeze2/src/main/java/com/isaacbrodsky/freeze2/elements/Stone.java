package com.isaacbrodsky.freeze2.elements;

import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;

import java.util.Random;

public class Stone extends ElementImpl {
    private final Random random = new Random();

    @Override
    public GraphicsBlock draw(GameController game, Board board, int x, int y, Tile t, Element e) {
        // Changes its color in the tick
        return new GraphicsBlock(t.getColor(), random.nextInt(26) + 0x41);
    }
}
