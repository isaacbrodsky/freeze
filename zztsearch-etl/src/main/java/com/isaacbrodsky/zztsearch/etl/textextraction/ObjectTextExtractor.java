package com.isaacbrodsky.zztsearch.etl.textextraction;

import com.isaacbrodsky.freeze.elements.Scroll;
import com.isaacbrodsky.freeze.elements.ZObject;
import com.isaacbrodsky.zztsearch.etl.text.BoardGameText;
import com.isaacbrodsky.zztsearch.etl.text.ObjectGameText;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ObjectTextExtractor {
    private final BoardGameText board;
    private final ZObject object;

    public ObjectGameText text() {
        if (object.getStats() == null)
            return null;

        String[] oop = object.getStats().getOop().split("\r");

        String name = null;
        List<String> comments = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> texts = new ArrayList<>();
        for (int i = 0; i < oop.length; i++) {
            final String line = oop[i];
            if (i == 0 && line.startsWith("@")) {
                name = line.substring(1);
            } else if (line.startsWith(":")) {
                labels.add(line.substring(1));
            } else if (line.startsWith("'")) {
                comments.add(line.substring(1));
            } else if (line.startsWith("#")
                    || line.startsWith("/")
                    || line.startsWith("?")) {
                // skip - instructions
                // TODO this possibly skips text like "/i test"
            } else if (line.startsWith("$")) {
                texts.add(line.substring(1));
            } else if (line.startsWith("!")) {
                int foundBreak = line.indexOf(';');
                if (foundBreak == -1) {
                    texts.add(line);
                } else {
                    texts.add(line.substring(foundBreak + 1));
                }
            } else {
                texts.add(line);
            }
        }

        return new ObjectGameText(board,
                name,
                texts.stream()
                        .filter(l -> l.trim().length() > 0)
                        .collect(Collectors.toList()),
                object instanceof Scroll,
                comments,
                labels);
    }
}

