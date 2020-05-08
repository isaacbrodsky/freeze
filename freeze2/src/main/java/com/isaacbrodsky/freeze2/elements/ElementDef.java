package com.isaacbrodsky.freeze2.elements;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ElementDef {
    @Builder.Default
    public int character = ' ';
    @Builder.Default
    public int color = 0xFF;
    @Builder.Default
    public boolean destructible = false;
    @Builder.Default
    public boolean pushable = false;
    @Builder.Default
    public boolean visibleInDark = false;
    @Builder.Default
    public boolean placeableOnTop = false;
    @Builder.Default
    public boolean hasDrawProc = false;
    @Builder.Default
    public int cycle = -1;
    @Builder.Default
    public EditorCategory editorCategory = EditorCategory.NONE;
    @Builder.Default
    public boolean walkable = false;
    @Builder.Default
    public char editorShortcut = 0;
    @Builder.Default
    public String name = "";
    @Builder.Default
    public String categoryName = "";
    @Builder.Default
    public String param1Name = "";
    @Builder.Default
    public String param2Name = "";
    @Builder.Default
    public String paramBulletTypeName = "";
    @Builder.Default
    public String paramBoardName = "";
    @Builder.Default
    public String paramDirName = "";
    @Builder.Default
    public String paramTextName = "";
    @Builder.Default
    public int scoreValue = 0;

    public static final int COLOR_SPECIAL_MIN = 0xF0;
    public static final int COLOR_CHOICE_ON_BLACK = 0xFF;
    public static final int COLOR_WHITE_ON_CHOICE = 0xFE;
    public static final int COLOR_CHOICE_ON_CHOICE = 0xFD;

    @Override
    public String toString() {
        return "Character: " + character +
                "\r\nColor: " + color +
                "\r\nDestructible: " + destructible +
                "\r\nPushable: " + pushable +
                "\r\nVisible in dark: " + visibleInDark +
                "\r\nPlaceable on top: " + placeableOnTop +
                "\r\nHas draw proc: " + hasDrawProc +
                "\r\nCycle: " + cycle +
                "\r\nEditor category: " + editorCategory +
                "\r\nWalkable: " + walkable +
                "\r\nEditor shortcut: " + editorShortcut +
                "\r\nName: " + name +
                "\r\nCategory name: " + categoryName +
                "\r\nParam 1 name: " + param1Name +
                "\r\nParam 2 name: " + param2Name +
                "\r\nParam bullet type name: " + paramBulletTypeName +
                "\r\nParam board name: " + paramBoardName +
                "\r\nParam dir name: " + paramDirName +
                "\r\nParam text name: " + paramTextName +
                "\r\nScore value: " + scoreValue;
    }
}
