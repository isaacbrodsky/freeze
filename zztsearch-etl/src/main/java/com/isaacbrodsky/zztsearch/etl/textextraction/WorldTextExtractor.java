package com.isaacbrodsky.zztsearch.etl.textextraction;

import com.google.common.collect.ImmutableList;
import com.isaacbrodsky.freeze.EmuMode;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.game.SuperZGameController;
import com.isaacbrodsky.freeze.game.ZGameController;
import com.isaacbrodsky.zztsearch.etl.text.GameText;
import com.isaacbrodsky.zztsearch.etl.text.WorldGameText;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@AllArgsConstructor
public class WorldTextExtractor {
    /**
     * Path to the world file
     */
    private final String path;
    /**
     * Name of the world file
     */
    private final String world;

    private final GameController controller;

    public Collection<GameText> allText() {
        final WorldGameText world = new WorldGameText(path, this.world, getName(), getMode(), isSave(), getFlags());
        final List<Board> boards = controller.getBoardList();
        return ImmutableList.<GameText>builder()
                .addAll(
                IntStream.range(0, boards.size())
                        .filter(index -> boards.get(index) != null)
                        .mapToObj(index -> new BoardTextExtractor(world, boards.get(index), index))
                        .map(BoardTextExtractor::allText)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()))
                .add(world)
                .build();
    }

    private String getName() {
        return controller.getState().gameName;
    }

    private EmuMode getMode() {
        if (controller instanceof ZGameController) {
            return EmuMode.ZZT;
        }
        if (controller instanceof SuperZGameController) {
            return EmuMode.SUPERZZT;
        }
        // Invalid
        return EmuMode.DEFAULT;
    }

    private boolean isSave() {
        return controller.getState().isSave != 0;
    }

    private List<String> getFlags() {
        return Collections.unmodifiableList(Stream.of(controller.getState().flags)
                .filter(f -> f != null)
                .collect(Collectors.toList()));
    }
}
