package com.isaacbrodsky.freeze2.game;

import com.fasterxml.jackson.annotation.*;
import com.isaacbrodsky.freeze2.EmuMode;
import com.isaacbrodsky.freeze2.elements.Element;
import com.isaacbrodsky.freeze2.elements.Elements;
import com.isaacbrodsky.freeze2.graphics.Renderer;
import com.isaacbrodsky.freeze2.menus.UIInteraction;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(ZGameController.class),
        @JsonSubTypes.Type(SuperZGameController.class)
})
public interface GameController {
    @JsonIgnore
    boolean isPaused();
    @JsonIgnore
    boolean isMonochrome();

    void setBoard(int index);
    @JsonIgnore
    Board getBoard();
    @JsonIgnore
    int getBoardIndex();

    @JsonIgnore
    Elements getElements();
    default Element resolveElement(int type) {
        return getElements().resolveType(type);
    }

    @JsonIgnore
    EmuMode getEmuMode();
    int statsLimit();

    GameState getState();
    int currentTick();

    @JsonProperty("boards")
    List<Board> getBoardList();
    @JsonProperty("boards")
    void setBoardList(List<Board> boards);

    void startPlaying();
    void runSimulation(long elapsed);

    void render(Renderer renderer, boolean flashing);
    @JsonIgnore
    UIInteraction getMenu();

    @JsonIgnore
    int getXViewOffset();
    @JsonIgnore
    int getYViewOffset();
}
