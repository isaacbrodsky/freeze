package com.isaacbrodsky.freeze2.game;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.isaacbrodsky.freeze2.EmuMode;
import com.isaacbrodsky.freeze2.elements.Element;
import com.isaacbrodsky.freeze2.elements.Elements;
import com.isaacbrodsky.freeze2.elements.ZElement;
import com.isaacbrodsky.freeze2.elements.ZElements;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;
import com.isaacbrodsky.freeze2.graphics.Renderer;
import com.isaacbrodsky.freeze2.menus.UIInteraction;

import java.util.List;
import java.util.Random;

@JsonTypeName("ZZT")
public class ZGameController implements GameController {
    public static final int TORCH_DIST_SQR = 50;

    private boolean paused;
    private int currentBoardIndex;
    private Board currentBoard;
    private List<Board> boards;
    private GameState state;
    private int currentTick;
    private UIInteraction currentMenu;

    public ZGameController() {
        this.paused = false;
    }

    public ZGameController(GameState state, List<Board> boards) {
        this();
        this.boards = boards;
        this.currentBoardIndex = 0;
        this.currentBoard = boards.get(currentBoardIndex);
        this.state = state;
        this.currentTick = 0;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public boolean isMonochrome() {
        // TODO
        return false;
    }

    @Override
    public void setBoard(int index) {
        this.currentBoardIndex = index;
        this.currentBoard = boards.get(currentBoardIndex);
    }

    @Override
    public int getBoardIndex() {
        return currentBoardIndex;
    }

    @Override
    public Board getBoard() {
        return currentBoard;
    }

    @Override
    public GameState getState() {
        return state;
    }

    @Override
    public Elements getElements() {
        return ZElements.INSTANCE;
    }

    @Override
    public int currentTick() {
        return currentTick;
    }

    @Override
    public List<Board> getBoardList() {
        return boards;
    }

    @Override
    public void setBoardList(List<Board> boards) {
        this.boards = boards;
    }

    @Override
    public void startPlaying() {
        setBoard(getState().startBoard);
    }

    @Override
    public void runSimulation(long elapsed) {
        if (currentMenu != null) {
            UIInteraction oldMenu = currentMenu;
            currentMenu.tick();
            boolean keepMenu = currentMenu.stillAlive();
            if (!keepMenu && oldMenu == currentMenu)
                currentMenu = null;
        } else {
            if (isPaused()) {
                return;
            }

            currentTick++;

            currentBoard.tick();
        }
    }

    @Override
    public void render(Renderer renderer, boolean blinking) {
        if (currentBoard == null) {
            renderer.renderText(0, 0, "null board.", Renderer.SYS_COLOR);
            return;
        }

        // scrolling support
        int px = getXViewOffset();
        int py = getYViewOffset();

        for (int x = px + 1; x <= px + Renderer.DISPLAY_WIDTH; x++) {
            for (int y = py + 1; y <= py + Renderer.DISPLAY_HEIGHT; y++) {
                Tile t = currentBoard.tileAt(x, y);
                Element e = resolveElement(t.getType());
                GraphicsBlock block = e.impl().draw(this, currentBoard, x, y, t, e);
                renderer.set(x - 1 - px, y - 1 - py, block);
            }
        }

        if (blinking && isPaused()) {
            Stat s = currentBoard.getStats().get(0);
            renderer.set(s.x - 1, s.y - 1, new GraphicsBlock(0, 0));
        }

        // TODO board is dark
        // TODO message

        if (currentMenu != null) {
            currentMenu.render(renderer, 0, true);
        }
    }

    @Override
    public UIInteraction getMenu() {
        return currentMenu;
    }

    @Override
    public int getXViewOffset() {
        return 0;
    }

    @Override
    public int getYViewOffset() {
        return 0;
    }

    @Override
    public EmuMode getEmuMode() {
        return EmuMode.ZZT;
    }

    @Override
    public int statsLimit() {
        return 150;
    }
}
