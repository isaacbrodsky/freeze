package com.isaacbrodsky.freeze2.menus.stateditor;

import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;
import com.isaacbrodsky.freeze2.graphics.Renderer;
import com.isaacbrodsky.freeze2.menus.MenuCallback;
import com.isaacbrodsky.freeze2.menus.UIInteraction;

import java.awt.event.KeyEvent;
import java.util.List;

public class MatrixInputForm implements UIInteraction {
    private static final int WIDTH = 32;
    private static final int HEIGHT = 8;

    private final List<GraphicsBlock> options;
    private final MenuCallback<Integer> callback;

    private String title;
    private int val;
    private boolean stillAlive;
    private String cmd;

    public MatrixInputForm(String title, int curr, List<GraphicsBlock> options, MenuCallback<Integer> callback) {
        if (options.size() != WIDTH * HEIGHT) {
            throw new IllegalArgumentException("Invalid number of options for CharInputForm");
        }
        this.stillAlive = true;
        this.title = title;
        this.val = curr;
        this.options = options;
        this.callback = callback;
    }

    @Override
    public String getSelectedLabel() {
        return Integer.toString(val);
    }

    @Override
    public String getSelectedText() {
        return Integer.toString(val);
    }

    @Override
    public boolean keyPress(int key) {
        switch (key) {
            case KeyEvent.VK_ESCAPE:
                stillAlive = false;
                cmd = "CANCEL";
                return true;
            case KeyEvent.VK_ENTER:
                stillAlive = false;
                cmd = "SUBMIT";
                return true;
            case KeyEvent.VK_RIGHT:
                val++;
                if (val >= WIDTH * HEIGHT)
                    val = 0;
                return true;
            case KeyEvent.VK_LEFT:
                val--;
                if (val < 0)
                    val = WIDTH * HEIGHT - 1;
                return true;
            case KeyEvent.VK_UP:
                val -= WIDTH;
                if (val < 0)
                    val += WIDTH * HEIGHT;
                return true;
            case KeyEvent.VK_DOWN:
                val += 32;
                if (val > WIDTH * HEIGHT - 1)
                    val -= WIDTH * HEIGHT;
                return true;
        }
        return false;
    }

    @Override
    public void render(Renderer renderer, int yoff, boolean focused) {
        int targetX = val % WIDTH;
        int targetY = val / WIDTH;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int idx = y * WIDTH + x;
                GraphicsBlock block = options.get(idx);
                int dx = Math.abs(x - targetX);
                int dy = Math.abs(y - targetY);
                if ((dx == 1 && dy <= 1) || (dy == 1 && dx <= 1)) {
                    block = new GraphicsBlock(0x0F, 219);
                }
                renderer.renderText(x, yoff + y, block.getCharIndex(), block.getColoring());
            }
        }
    }

    @Override
    public boolean stillAlive() {
        return stillAlive;
    }

    @Override
    public void tick() {
        if (!stillAlive) {
            if (callback != null) {
                callback.menuCommand(cmd, val);
            }
        }
    }

    @Override
    public UIInteraction getFocusedInteraction() {
        return this;
    }
}
