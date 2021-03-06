package com.isaacbrodsky.zztsearch.etl.text;

import lombok.Value;

import java.util.List;

@Value
public class ObjectGameText implements GameText {
    /**
     * Up reference to board and thereby world
     */
    private BoardGameText board;

    /**
     * Name of the object, or null.
     */
    private String name;

    /**
     * Each discrete thing the object can say
     */
    private List<String> texts;

    /**
     * The object's type (SCROLL, OBJECT)
     */
    private String type;

    /**
     * Any comments (zap'd labels) in this objects script
     */
    private List<String> comments;

    /**
     * Any labels in this object's script
     */
    private List<String> labels;

    // TODO does position matter?

    @Override
    public void accept(GameTextVisitor visitor) {
        visitor.visit(this);
    }
}
