package com.isaacbrodsky.zztsearch.etl.textextraction;

import com.isaacbrodsky.freeze.elements.ZObject;
import com.isaacbrodsky.freeze.game.Board;
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

        board.getElementList().stream()
                // Scrolls extend ZObject, so that's ok
                .filter(e -> e instanceof ZObject)
                .map(e -> (ZObject) e)
                .map(e -> new ObjectTextExtractor(boardGameText, e))
                .map(ObjectTextExtractor::text)
                .filter(t -> t != null)
                .forEach(text::add);

        text.addAll(new TextElementExtractor(boardGameText, board).allText());

        return Collections.unmodifiableList(text);
    }
}
