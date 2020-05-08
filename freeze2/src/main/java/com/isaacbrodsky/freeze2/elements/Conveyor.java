package com.isaacbrodsky.freeze2.elements;

import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;

public class Conveyor extends ElementImpl {
    private final int[] CW_CHARS = {179, 47, 196, 92};
    private final int[] CCW_CHARS = {92, 196, 47, 179};

    private final Direction direction;
    private final int cycle;

    public Conveyor(Direction direction, int cycle) {
        this.direction = direction;
        this.cycle = cycle;
    }

    @Override
    public GraphicsBlock draw(GameController game, Board board, int x, int y, Tile t, Element e) {
        int idx = (game.currentTick() / cycle) % 4;
        int ch;
        switch (direction) {
            case CW:
                ch = CW_CHARS[idx];
                break;
            case CCW:
                ch = CCW_CHARS[idx];
                break;
            default:
                throw new RuntimeException("Illegal direction");
        }

        return new GraphicsBlock(t.getColor(), ch);
    }

    enum Direction {
        CW,
        CCW
    }
}
