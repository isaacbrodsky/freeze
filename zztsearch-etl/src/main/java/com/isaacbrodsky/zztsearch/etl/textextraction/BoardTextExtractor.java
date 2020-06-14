package com.isaacbrodsky.zztsearch.etl.textextraction;

import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.zztsearch.etl.text.BoardGameText;
import com.isaacbrodsky.zztsearch.etl.text.GameText;
import com.isaacbrodsky.zztsearch.etl.text.WorldGameText;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class BoardTextExtractor {
    private final WorldGameText world;
    private final GameController game;
    private final Board board;
    private final int index;

    public Collection<GameText> allText() {
        final List<GameText> text = new ArrayList<>();

        final BoardHasher hasher = new BoardHasher(board);

        final BoardGameText boardGameText = new BoardGameText(world,
                board.getState().boardName,
                board.getState().message,
                index,
                hasher.compute());
        text.add(boardGameText);

        board.getStats().stream()
                .map(s -> new ObjectTextExtractor(boardGameText, resolveTileName(s.x, s.y), s))
                .map(ObjectTextExtractor::text)
                .filter(t -> t != null)
                .forEach(text::add);

        text.addAll(new TextElementExtractor(boardGameText, game, board).allText());

        return Collections.unmodifiableList(text);
    }

    private String resolveTileName(int x, int y) {
        if (!board.inBounds(x, y)) {
            return null;
        }
        return game.resolveElement(board.tileAt(x, y).getType()).toString();
    }
}
