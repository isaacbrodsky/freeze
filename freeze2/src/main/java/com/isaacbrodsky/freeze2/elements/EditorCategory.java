package com.isaacbrodsky.freeze2.elements;

public enum EditorCategory {
    NONE(true),
    ITEM(false),
    CREATURE(false),
    TERRAIN(false),
    UGLIES(false),
    TERRAIN2(false);

    private final boolean hidden;

    EditorCategory(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    public static EditorCategory forKey(char ch) {
        switch (ch) {
            case '1':
                return ITEM;
            case '2':
                return CREATURE;
            case '3':
                return TERRAIN;
            case '4':
                return UGLIES;
            case '5':
                return TERRAIN2;
        }
        return NONE;
    }
}
