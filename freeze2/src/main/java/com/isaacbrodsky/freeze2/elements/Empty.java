package com.isaacbrodsky.freeze2.elements;

import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;

public class Empty extends ElementImpl {
    @Override
    public GraphicsBlock draw(GameController game, Board board, int x, int y, Tile t, Element e) {
        // 0x0F is important because of the way ZZT handles monochrome
        return new GraphicsBlock(0x0F, 0);
    }
}
