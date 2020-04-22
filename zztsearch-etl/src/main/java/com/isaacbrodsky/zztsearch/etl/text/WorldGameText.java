package com.isaacbrodsky.zztsearch.etl.text;

import com.isaacbrodsky.freeze.EmuMode;
import lombok.Value;

import java.util.List;

/**
 * Represents the world part of the game text - name of the file,
 * whether it's SuperZZT or not, etc.
 */
@Value
public class WorldGameText implements GameText {
    /**
     * Path to world file
     */
    private String path;

    /**
     * Name of world file
     */
    private String world;

    /**
     * Name embedded in the world file
     */
    private String name;

    /**
     * Whether this is ZZT or SuperZZT
     */
    private EmuMode mode;

    /**
     * True if this is a save file rather than a world file
     */
    private boolean isSave;

    /**
     * Any starting flags set on this world.
     */
    private List<String> flags;

    // TODO: Has modified starting parameters

    @Override
    public void accept(GameTextVisitor visitor) {
        visitor.visit(this);
    }
}
