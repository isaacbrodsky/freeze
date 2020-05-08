package com.isaacbrodsky.freeze2.elements;

import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.game.Stat;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;

public class SpinningGun extends ElementImpl {
    @Override
    public GraphicsBlock draw(GameController game, Board board, int x, int y, Tile t, Element e) {
        byte ch;
        switch (game.currentTick() % 8) {
            case 0:
            case 1:
                ch = 24;
                break;
            case 2:
            case 3:
                ch = 26;
                break;
            case 4:
            case 5:
                ch = 25;
                break;
            default:
                ch = 27;
                break;
        }
        return new GraphicsBlock(t.getColor(), ch);
    }
}
