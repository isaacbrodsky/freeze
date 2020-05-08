package com.isaacbrodsky.freeze2.menus.stateditor;

import com.isaacbrodsky.freeze2.elements.Element;
import com.isaacbrodsky.freeze2.elements.ElementDef;
import com.isaacbrodsky.freeze2.game.Stat;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.game.editor.Cursor;
import com.isaacbrodsky.freeze2.game.editor.EditorController;
import com.isaacbrodsky.freeze2.game.editor.ElementBuffer;
import com.isaacbrodsky.freeze2.menus.MenuCallback;
import com.isaacbrodsky.freeze2.menus.MultiInput;
import com.isaacbrodsky.freeze2.menus.TextInput;
import com.isaacbrodsky.freeze2.menus.UIInteraction;
import com.isaacbrodsky.freeze2.utils.StringUtils;
import com.isaacbrodsky.freeze2.utils.TimeAndMathUtils;

import java.util.ArrayList;
import java.util.List;

public class TileEditor {
    public static UIInteraction makeTileInspector(EditorController editor,
                                                  ElementBuffer e,
                                                  Cursor cur,
                                                  MenuCallback<ElementBuffer> onFinishEdit
    ) {
        final Tile tile = e.tile;
        final Element element;
        if (tile == null) {
            element = editor.getElements().unknownElement();
        } else {
            element = editor.resolveElement(tile.getType());
        }
        ElementDef def = element.def();

        List<UIInteraction> ui = new ArrayList<>();

        ui.add(new TileInput("Type:", tile.getType(), editor));
        ui.add(new MatrixInput("Color:", tile.getColor(), MatrixInput.makeColorList()));

        return new MultiInput("Tile Inspector "
                + element + " @ " + cur.toString() + "\n"
                + "(\001ENTER\002 to save, \001ESC\002 to cancel,"
                + " \001UP\002 and \001DOWN\002 to navigate)", ui,
                new MenuCallback() {

                    @Override
                    public void menuCommand(String cmd, Object rider) {
                        if (cmd != null && cmd.equals("SUBMIT")) {
                            try {
                                ArrayList<String> results = (ArrayList<String>) rider;

                                int newType = TimeAndMathUtils
                                        .parseInt(results.get(0));
                                int newColor = TimeAndMathUtils
                                        .parseInt(results.get(1));
                                Tile newTile = new Tile(newType, newColor);
                                ElementBuffer newBuffer = new ElementBuffer(newTile, e.stats);

                                onFinishEdit.menuCommand(cmd, newBuffer);
                            } catch (Exception e) {
                                e.printStackTrace();
                                editor.reportError("Problem setting tile info\r\n"
                                        + e.toString());
                            }
                        }
                    }

                });
    }
}
