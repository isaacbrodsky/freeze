package com.isaacbrodsky.freeze2.game.editor;

import com.isaacbrodsky.freeze2.game.Stat;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.Optional;

@Value
@AllArgsConstructor
public class ElementBuffer {
    public Tile tile;
    public List<Stat> stats;
    public Optional<GraphicsBlock> graphics;

    public ElementBuffer(Tile tile, List<Stat> stats) {
        this(tile, stats, Optional.empty());
    }

    public ElementBuffer(Tile tile, List<Stat> stats, GraphicsBlock graphics) {
        this(tile, stats, Optional.of(graphics));
    }
}
