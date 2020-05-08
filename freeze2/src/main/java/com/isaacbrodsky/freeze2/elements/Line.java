package com.isaacbrodsky.freeze2.elements;

import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.game.Neighbor;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;

public class Line extends ElementImpl {
    private static final int[] CHARS = {
            249,
            208,
            210,
            186,
            181,
            188,
            187,
            185,
            198,
            200,
            201,
            204,
            205,
            202,
            203,
            206
    };

    @Override
    public GraphicsBlock draw(GameController game, Board board, int x, int y, Tile t, Element e) {
        int v = 1;
        int shift = 1;
        for (int i = 0; i < 4; i++) {
            Tile neighbor = board.tileAt(x + Neighbor.NEIGHBOR_DELTA_X[i], y + Neighbor.NEIGHBOR_DELTA_Y[i]);
            if (neighbor.getType() == CommonElements.LINE || neighbor.getType() == CommonElements.BOARD_EDGE) {
                v = v + shift;
            }
            shift = shift << 1;
        }

        int ch = CHARS[v - 1];
        return new GraphicsBlock(t.getColor(), ch);
    }
}
