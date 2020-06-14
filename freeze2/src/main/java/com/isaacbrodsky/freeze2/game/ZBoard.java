package com.isaacbrodsky.freeze2.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.isaacbrodsky.freeze2.elements.CommonElements;
import com.isaacbrodsky.freeze2.elements.Element;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@JsonTypeName("ZZT")
public class ZBoard implements Board {
    // TODO: ZZT uses 1-indexed boards for display, although the
    // board is really 0 indexed (the extra rows and columns are for
    // the board edge objects.) It's important to differentiate which
    // coordinate system is being used.
    public static final int BOARD_WIDTH = 60;
    public static final int BOARD_HEIGHT = 25;

    private static final Stat DEFAULT_STAT = new Stat(0, 0);

    @Override
    public int getWidth() {
        return BOARD_WIDTH;
    }
    @Override
    public int getHeight() {
        return BOARD_HEIGHT;
    }

    private Tile[][] tiles;
    private List<Stat> stats;
    private BoardState state;

    @JsonCreator
    public ZBoard(
            @JsonProperty("state") BoardState state,
            @JsonProperty("tiles") Tile[][] tiles,
            @JsonProperty("stats") List<Stat> stats
    ) {
        this.state = state;
        this.tiles = tiles;
        this.stats = stats;
    }

    @Override
    public Tile[][] getTiles() {
        return tiles;
    }

    @Override
    public Tile tileAt(int x, int y) {
        if (x == 0 || x == getWidth() + 1
                || y == 0 || y == getHeight() + 1) {
            return new Tile(CommonElements.BOARD_EDGE, 0);
        }
        return tiles[x - 1][y - 1];
    }

    protected Tile tileAt(Stat s) {
        return tileAt(s.x, s.y);
    }

    @Override
    public boolean inBounds(int x, int y) {
        return !(x < 0 || x > getWidth() + 1
                || y < 0 || y > getHeight() + 1);
    }

    @Override
    public Stream<Tile> tileStream() {
        IntStream rows = IntStream.range(0, getHeight());
        return rows
                .mapToObj(row -> {
                    IntStream cols = IntStream.range(0, getWidth());
                    return cols.mapToObj(col -> tileAt(col + 1, row + 1));
                })
                .flatMap(row -> row);
    }

    @Override
    public void putTileAndStats(int x, int y, Tile t, Stat... newStats) {
        tiles[x - 1][y - 1] = t;
        Collection<Stat> oldStats = stats.stream()
                .filter(s -> s.x == x && s.y == y)
                .collect(Collectors.toList());
        stats.removeAll(oldStats);
        List<Stat> retargetedStats = Arrays.asList(newStats).stream()
                .map(s -> {
                    Stat newStat = new Stat(s);
                    newStat.x = x;
                    newStat.y = y;
                    return newStat;
                })
                .collect(Collectors.toList());
        stats.addAll(retargetedStats);
    }

    @Override
    public List<Stat> getStats() {
        return stats;
    }

    @Override
    public Stat statAt(int x, int y) {
        // TODO: When the stat is not found, -1 is returned. This leads to an OOB reference.
        // Should mimick this stat
        return stats.stream()
                .filter(s -> s.x == x && s.y == y)
                .findFirst()
                .orElse(DEFAULT_STAT);
    }

    @Override
    public int statIdAt(int x, int y) {
        int i = 0;
        for (Stat s : stats) {
            if (s.x == x && s.y == y) {
                return i;
            }
            i++;
        }
        return -1;
    }

    @Override
    public Stat getPlayer() {
        return stats.size() > 0 ? stats.get(0) : null;
    }

    @Override
    public void moveStat(int idx, int x, int y) {
        Stat s = stats.get(idx);

        Tile oldUnder = s.under;
        Tile old = tileAt(s);

        s.under = tileAt(x, y);

        if (tileAt(s).getType() == CommonElements.PLAYER) {
            tiles[x - 1][y - 1] = old;
        } else if (tileAt(x, y).getType() == CommonElements.EMPTY) {
            tiles[x - 1][y - 1] = new Tile(old.getType(), old.getColor() & 0x0F);
        } else {
            tiles[x - 1][y - 1] = new Tile(old.getType(), (old.getColor() & 0x0F) | (tiles[x - 1][y - 1].getColor() & 0x70));
        }

        tiles[s.x - 1][s.y - 1] = oldUnder;

//        int oldX = s.x;
//        int oldY = s.y;
        s.x = x;
        s.y = y;

        // TODO redraw
    }

    /**
     * Performs board ticking actions. Should be called as a part of the game
     * simulation system.
     */
    @Override
    public void tick() {

    }

    @Override
    public BoardState getState() {
        return state;
    }

    protected Tile[][] copyTileArray() {
        Tile[][] tilesCopy = new Tile[getWidth()][getHeight()];
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                tilesCopy[x][y] = tiles[x][y];
            }
        }
        return tilesCopy;
    }

    @Override
    public Board copyBoard() {
        List<Stat> statsCopy = stats.stream().map(Stat::clone).collect(Collectors.toList());

        return new ZBoard(
                new BoardState(state),
                copyTileArray(),
                statsCopy
        );
    }
}
