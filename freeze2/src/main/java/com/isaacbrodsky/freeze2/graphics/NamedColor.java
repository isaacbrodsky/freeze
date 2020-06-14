package com.isaacbrodsky.freeze2.graphics;

import java.awt.*;

public enum NamedColor {
    // Order is the code of the color
    BLACK(0, 0, 0),
    DARKBLUE(0, 0, 168),
    DARKGREEN(0, 168, 0),
    DARKCYAN(0, 168, 168),
    DARKRED(168, 0, 0),
    DARKPURPLE(168, 0, 168),
    BROWN(168, 87, 0),
    GRAY(168, 168, 168),
    DARKGRAY(87, 87, 87),
    BLUE(87, 87, 255, true),
    GREEN(87, 255, 87, true),
    CYAN(87, 255, 255, true),
    RED(255, 87, 87, true),
    PURPLE( 255, 87, 255, true),
    YELLOW(255, 255, 87, true),
    WHITE(255, 255, 255, true);

    private final Color color;
    public boolean oopNameable;

    NamedColor(int r, int g, int b) {
        this.color = new Color(r, g, b);
    }

    NamedColor(int r, int g, int b, boolean oopNameable) {
        this(r, g, b);
        this.oopNameable = oopNameable;
    }

    public Color color() {
        return color;
    }

    public static NamedColor colorFromCode(int code) {
        if (code >= values().length || code < 0)
            throw new ArrayIndexOutOfBoundsException("Invalid color code: "
                    + code);

        return values()[code];
    }
}
