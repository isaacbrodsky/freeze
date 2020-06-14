package com.isaacbrodsky.freeze2.elements;

import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;

public class DragonPup extends ElementImpl {
    @Override
    public GraphicsBlock draw(GameController game, Board board, int x, int y, Tile t, Element e) {
        int ch = e.def().character;
        switch (game.currentTick() & 3) {
            case 0:
            case 2:
                ch = 148;
                break;
            case 1:
                ch = 162;
                break;
            case 3:
                ch = 149;
                break;
        }
        return new GraphicsBlock(t.getColor(), ch);
    }
}
