package com.isaacbrodsky.freeze2.elements;

import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;
import com.isaacbrodsky.freeze2.graphics.NamedColor;

public class Text extends ElementImpl {
    @Override
    public GraphicsBlock draw(GameController game, Board board, int x, int y, Tile t, Element e) {
        int textMin = game.getElements().getTextMin();
        if (t.getType() == textMin + 6) { // TEXT_WHITE
            return new GraphicsBlock(0x0F, t.getColor());
        } else if (game.isMonochrome()) {
            return new GraphicsBlock((t.getType() - textMin + 1) << 4,
                    t.getColor());
        } else {
            return new GraphicsBlock(((t.getType() - textMin + 1) << 4) | 0x0F,
                    t.getColor());
        }
    }
}
