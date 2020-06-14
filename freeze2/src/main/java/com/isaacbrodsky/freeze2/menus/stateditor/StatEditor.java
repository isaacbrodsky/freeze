package com.isaacbrodsky.freeze2.menus.stateditor;

import com.isaacbrodsky.freeze2.elements.CommonElements;
import com.isaacbrodsky.freeze2.elements.Element;
import com.isaacbrodsky.freeze2.elements.ElementDef;
import com.isaacbrodsky.freeze2.game.Stat;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.game.editor.EditorController;
import com.isaacbrodsky.freeze2.menus.*;
import com.isaacbrodsky.freeze2.utils.StringUtils;
import com.isaacbrodsky.freeze2.utils.TimeAndMathUtils;

import java.util.ArrayList;
import java.util.List;

public class StatEditor {
    public static UIInteraction makeStatInspector(EditorController editor,
                                                  Tile tile,
                                                  Stat stat,
                                                  MenuCallback<Stat> onFinishEdit
    ) {
        final Element element;
        if (tile == null) {
            element = editor.getElements().unknownElement();
        } else {
            element = editor.resolveElement(tile.getType());
        }
        ElementDef def = element.def();

        String param1text = "Parameter 1:";
        if (!StringUtils.isNullOrEmpty(def.param1Name))
            param1text += " (" + def.param1Name + ")";
        String param2text = "Parameter 2:";
        if (!StringUtils.isNullOrEmpty(def.paramBulletTypeName))
            param2text += " (" + def.paramBulletTypeName + ")";
        if (!StringUtils.isNullOrEmpty(def.param2Name))
            param2text += " (" + def.param2Name + ")";
        String param3text = "Parameter 3:";
        if (!StringUtils.isNullOrEmpty(def.paramBoardName))
            param3text += " (" + def.paramBoardName + ")";
        String stepXtext = "Step X:";
        if (!StringUtils.isNullOrEmpty(def.paramDirName))
            stepXtext += " (" + def.paramDirName + ")";
        String stepYtext = "Step Y:";
        String ooptext = "OOP:";
        if (!StringUtils.isNullOrEmpty(def.paramTextName))
            ooptext += " (" + def.paramTextName + ")";

        List<UIInteraction> ui = new ArrayList<>();

        if (!StringUtils.isNullOrEmpty(def.paramTextName))
            ui.add(new MatrixInput(param1text, stat.p1, MatrixInput.makeCharList()));
        else
            ui.add(new TextInput(param1text, Integer.toString(stat.p1), false));
        ui.add(new TextInput(param2text, Integer.toString(stat.p2), false));
        if (!StringUtils.isNullOrEmpty(def.paramBoardName))
            ui.add(new BoardInput(param3text, stat.p3, editor, true));
        else
            ui.add(new TextInput(param3text, Integer.toString(stat.p3), false));
        ui.add(new TextInput(stepXtext, Integer.toString(stat.stepX), false));
        ui.add(new TextInput(stepYtext, Integer.toString(stat.stepY), false));
        ui.add(new TextInput("Cycle:", Integer.toString(stat.cycle), false));
        ui.add(new TextInput("Current instruction:", Integer
                .toString(stat.currInstr), false));
        int binding = stat.oopLength >= 0 ? -1 : -stat.oopLength;
        ui.add(new ElementInput(editor, editor.getBoard(), "Binding (OOP length):", binding));
        ui.add(new EditorInput(ooptext, stat.oop));
        ui.add(new ElementInput(editor, editor.getBoard(), "Follower:", stat.follower));
        ui.add(new ElementInput(editor, editor.getBoard(), "Leader:", stat.leader));
        ui.add(new TileInput("Under type:", stat.under.getType(), editor));
        ui.add(new MatrixInput("Under color:", stat.under.getColor(), MatrixInput.makeColorList()));
        ui.add(new TextInput("X:", Integer.toString(stat.x), false));
        ui.add(new TextInput("Y:", Integer.toString(stat.y), false));

        return new MultiInput("Stats Inspector "
                + element + " @ (" + stat.x + "," + stat.y + ")\n"
                + "(\001ENTER\002 to save, \001ESC\002 to cancel,"
                + " \001UP\002 and \001DOWN\002 to navigate)", ui,
                new MenuCallback() {

                    @Override
                    public void menuCommand(String cmd, Object rider) {
                        if (cmd != null && cmd.equals("SUBMIT")) {
                            try {
                                ArrayList<String> results = (ArrayList<String>) rider;

                                int p1 = TimeAndMathUtils.parseInt(results
                                        .get(0));
                                int p2 = TimeAndMathUtils.parseInt(results
                                        .get(1));
                                int p3 = TimeAndMathUtils.parseInt(results
                                        .get(2));
                                int stepX = TimeAndMathUtils
                                        .parseInt(results.get(3));
                                int stepY = TimeAndMathUtils
                                        .parseInt(results.get(4));
                                int cycle = TimeAndMathUtils
                                        .parseInt(results.get(5));
                                int currInstr = TimeAndMathUtils
                                        .parseInt(results.get(6));
                                int oopLength = TimeAndMathUtils.parseInt(results.get(7));
                                String oop = results.get(8);
                                if (oopLength > 0) {
                                    // If the user entered binding, make that take priority.
                                    oopLength = -oopLength;
                                    // having OOP is not permitted in this case
                                    oop = "";
                                } else {
                                    oopLength = oop.length();
                                }
                                int follower = TimeAndMathUtils
                                        .parseInt(results.get(9));
                                int leader = TimeAndMathUtils
                                        .parseInt(results.get(10));

                                int underType = TimeAndMathUtils
                                        .parseInt(results.get(11));
                                int underColor = TimeAndMathUtils
                                        .parseInt(results.get(12));
                                Tile under = new Tile(underType, underColor);

                                int x = TimeAndMathUtils.parseInt(results
                                        .get(13));
                                int y = TimeAndMathUtils.parseInt(results
                                        .get(14));

                                Stat ns = new Stat(x, y, p1, p2, p3, stepX, stepY,
                                        cycle, follower, leader, currInstr, under, oopLength, oop);
                                onFinishEdit.menuCommand(cmd, ns);
                            } catch (Exception e) {
                                e.printStackTrace();
                                editor.reportError("Problem setting stat info\r\n"
                                        + e.toString());
                            }
                        }
                    }

                });
    }
}
