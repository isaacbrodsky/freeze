package com.isaacbrodsky.freeze2.game;

import com.isaacbrodsky.freeze2.elements.CommonElements;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class EmptyBoard implements Board {
    public static EmptyBoard INSTANCE = new EmptyBoard();

    private EmptyBoard() {}

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public Tile[][] getTiles() {
        return new Tile[0][0];
    }

    @Override
    public Tile tileAt(int x, int y) {
        return new Tile(CommonElements.BOARD_EDGE, 0);
    }

    @Override
    public boolean inBounds(int x, int y) {
        return false;
    }

    @Override
    public Stream<Tile> tileStream() {
        return Stream.empty();
    }

    @Override
    public void putTileAndStats(int x, int y, Tile t, Stat... stats) {

    }

    @Override
    public List<Stat> getStats() {
        return Collections.emptyList();
    }

    @Override
    public Stat statAt(int x, int y) {
        return null;
    }

    @Override
    public int statIdAt(int x, int y) {
        return -1;
    }

    @Override
    public Stat getPlayer() {
        return null;
    }

    @Override
    public void moveStat(int idx, int x, int y) {

    }

    @Override
    public void tick() {

    }

    @Override
    public BoardState getState() {
        return new BoardState();
    }

    @Override
    public Board copyBoard() {
        return INSTANCE;
    }
}
