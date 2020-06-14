package com.isaacbrodsky.freeze2.game.editor;

import com.isaacbrodsky.freeze2.elements.*;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.game.Stat;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.menus.*;
import com.isaacbrodsky.freeze2.utils.StringUtils;
import com.isaacbrodsky.freeze2.utils.TimeAndMathUtils;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AddElementUtils {
    @Value
    public static class Selection {
        String key;
        String value;
    }

    public static String makeSelectionList(List<Selection> options) {
        return makeSelectionList(options, false);
    }

    public static String makeSelectionList(List<Selection> options, boolean highlight) {
        int numRows = 22;
        int maxIdLength = -1;
        int maxNameLength = -1;

        ArrayList<StringBuilder> list = new ArrayList<>(numRows);

        int y = 0;
        for (int i = 0; i < options.size(); i++) {
            if (y == 0) {
                maxIdLength = IntStream
                        .range(i, Math.min(i + numRows, options.size()))
                        .map(j -> options.get(j).key.length())
                        .max()
                        .getAsInt();
                maxNameLength = IntStream
                        .range(i, Math.min(i + numRows, options.size()))
                        .map(j -> options.get(j).value.length())
                        .max()
                        .getAsInt();
            }

            Selection s = options.get(i);

            StringBuilder cur;
            if (list.size() <= y) {
                cur = new StringBuilder();
                list.add(y, cur);
            } else {
                cur = list.get(y);
            }

            String key = "";
            if (s.key != null) {
                key = s.key;
                if (highlight) {
                    key = '\001' + key + '\002';
                }
            }
            cur
                    .append(StringUtils.padLeft(key, maxIdLength + (highlight ? 2 : 0), ' '))
                    .append(" ")
                    .append(StringUtils.padRight(s.value, maxNameLength, ' '))
                    .append(" ");

            y++;
            if (y == numRows) {
                y = 0;
            }
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            StringBuilder cur = list.get(i);
            if (cur == null)
                continue;
            sb.append(cur).append('\n');
        }

        return sb.toString();
    }

    private static String makeElementList(GameController game, boolean hex) {
        List<Selection> elements = IntStream
                .range(0, 256)
                .mapToObj(game::resolveElement)
                .filter(e -> e != null && !(e.impl() instanceof Unknown))
                .map(e -> new Selection(Integer.toString(e.code(), hex ? 16 : 10), e.name()))
                .collect(Collectors.toList());
        return makeSelectionList(elements);
    }

    private static String makeCategoryList(EditorController editor) {
        List<EditorCategory> categories = Arrays.asList(EditorCategory.values())
                .stream()
                .filter(c -> !c.isHidden())
                .filter(c -> editor.getElements()
                            .allElements()
                            .stream()
                            .anyMatch(e -> e.def().editorCategory == c))
                .collect(Collectors.toList());
        List<Selection> selections = IntStream.range(0, categories.size())
                .mapToObj(i -> new Selection(Integer.toString(i + 1), categories.get(i).name()))
                .collect(Collectors.toList());
        return makeSelectionList(selections, true);
    }

    private static String makeCategorySelect(GameController game, EditorCategory category) {
        List<Selection> elements = game.getElements()
                .allElements()
                .stream()
                .filter(e -> e.def().editorCategory == category)
                .map(e -> new Selection(Character.toString(e.def().editorShortcut), e.name()))
                .collect(Collectors.toList());
        return makeSelectionList(elements, true);
    }

    /**
     *
     */
    public static UIInteraction selectCategory(EditorController editor, MenuCallback callback) {
        return new CharInput(editor.getEmuMode() + " Categories\n"
                + makeCategoryList(editor)
                + "Strike selection", callback, true, Integer
                .valueOf(editor.getColorIdx()), 0);
    }

    public static UIInteraction addElementByCategory(EditorController editor, char categoryChar, boolean useDefaultColor) {
        EditorCategory category = EditorCategory.forKey(categoryChar);
        return new CharInput(editor.getEmuMode() + " " + category + "\n"
                + makeCategorySelect(editor, category)
                + "Strike selection", makeAddElementByCategoryCallback(editor, category, useDefaultColor), true, Integer
                .valueOf(editor.getColorIdx()), 0);
    }

    public static UIInteraction addElementByCode(EditorController editor, boolean hex) {
        return new TextInput(editor.getEmuMode() + " Element Reference\n"
                + makeElementList(editor, hex)
                + "Element ID?", "", makeAddElementCallback(editor), true, Integer
                .valueOf(editor.getColorIdx()), 0);
    }

    public static MenuCallback makeAddElementCallback(EditorController editor) {
        return new MenuCallback() {
            @Override
            public void menuCommand(String cmd, Object rider) {
                if (cmd == null)
                    return;

                try {
                    int id = TimeAndMathUtils.parseInt(cmd);

                    int col = (Integer) rider;
                    Tile t = new Tile(id, col);
                    List<Stat> defaultStats = new ArrayList<>();
                    ElementDef def = editor.resolveElement(id).def();
                    if (def.cycle >= 0) {
                        Cursor cur = editor.getCursor();
                        defaultStats.add(createDefaultStat(def, cur.getX(), cur.getY()));
                    }
                    ElementBuffer e = new ElementBuffer(t, defaultStats);
                    editor.dropElement(e);
                } catch (Exception e) {
                    e.printStackTrace();
                    editor.reportError("Error adding element:\n" + e.toString());
                }
            }
        };
    }

    public static MenuCallback makeAddElementByCategoryCallback(EditorController editor,
                                                                EditorCategory category,
                                                                boolean useDefaultColor) {
        return new MenuCallback() {
            @Override
            public void menuCommand(String cmd, Object rider) {
                if (StringUtils.isNullOrEmpty(cmd))
                    return;

                try {
                    cmd = cmd.toUpperCase();
                    char c = cmd.charAt(0);
                    Optional<Element> element = editor.getElements().allElements()
                            .stream()
                            .filter(e -> e.def().editorCategory == category
                                && e.def().editorShortcut == c)
                            .findFirst();

                    element.ifPresent(e -> {
                        int col = (Integer) rider;
                        if (useDefaultColor) {
                            if (e.def().color == ElementDef.COLOR_WHITE_ON_CHOICE) {
                                col = ((col & 0xF) << 4) - 0x71;
                                if (col < 0 || col > 0xFF)
                                    col = 0x0F;
                            } else if (e.def().color == ElementDef.COLOR_CHOICE_ON_CHOICE) {
                                col = ((col - 8) * 0x11) + 8; // unused
                            } else if (e.def().color != ElementDef.COLOR_CHOICE_ON_BLACK) {
                                col = e.def().color;
                            }
                        }
                        if (e.code() == CommonElements.PLAYER && editor.getBoard().getStats().size() > 0) {
                            editor.getBoard().moveStat(0, editor.getCursor().getX(), editor.getCursor().getY());
                        } else {
                            Tile t = new Tile(e.code(), col);
                            List<Stat> defaultStats = new ArrayList<>();
                            ElementDef def = e.def();
                            if (def.cycle >= 0) {
                                Cursor cur = editor.getCursor();
                                defaultStats.add(createDefaultStat(def, cur.getX(), cur.getY()));
                            }
                            ElementBuffer buf = new ElementBuffer(t, defaultStats);
                            editor.dropElement(buf);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    editor.reportError("Error adding element:\n" + e.toString());
                }
            }
        };
    }

    public static Stat createDefaultStat(ElementDef def, int x, int y) {
        Stat stat = new Stat(x, y);
        stat.cycle = def.cycle;
        return stat;
    }
}
