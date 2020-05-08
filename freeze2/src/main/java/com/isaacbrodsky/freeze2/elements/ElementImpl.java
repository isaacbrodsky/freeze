package com.isaacbrodsky.freeze2.elements;

import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;

public class ElementImpl {
    public GraphicsBlock draw(GameController game, Board board, int x, int y, Tile t, Element e) {
        return new GraphicsBlock(t.getColor(), e.def().character);
    }

    public void tick(GameController game, Board board, int statId) {

    }

    public void touch(GameController game, Board board, int x, int y, int sourceStatId, int deltaX, int deltaY) {

    }
}
