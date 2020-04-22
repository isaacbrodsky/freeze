package com.isaacbrodsky.zztsearch.etl.text;

import lombok.Value;

/**
 * Game text from something written on the board
 */
@Value
public class ElementGameText implements GameText {
    private BoardGameText board;

    private String text;

    @Override
    public void accept(GameTextVisitor visitor) {
        visitor.visit(this);
    }
}
