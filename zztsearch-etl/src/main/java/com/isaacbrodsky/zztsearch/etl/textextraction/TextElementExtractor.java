package com.isaacbrodsky.zztsearch.etl.textextraction;

import com.isaacbrodsky.freeze2.elements.Element;
import com.isaacbrodsky.freeze2.elements.Text;
import com.isaacbrodsky.freeze2.game.Board;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.zztsearch.etl.text.BoardGameText;
import com.isaacbrodsky.zztsearch.etl.text.ElementGameText;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TextElementExtractor {
    private final BoardGameText boardGameText;
    private final GameController game;
    private final Board board;

    public Collection<ElementGameText> allText() {
        List<Set<Point>> blocks = new ArrayList<>();
        Set<Point> seen = new HashSet<>();
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                Point start = new Point(x, y);

                Tile t = board.tileAt(x + 1, y + 1);
                if (isText(t) && !seen.contains(start)) {
                    Set<Point> block = new HashSet<>();
                    dfs(block, x, y);
                    blocks.add(block);
                    seen.addAll(block);
                }
            }
        }

        return Collections.unmodifiableList(
                blocks.stream()
                        .map(b -> b.stream().sorted().collect(Collectors.toList()))
                        .map(b -> {
                            Point last = new Point(-1, -1);
                            boolean vertical = isVertical(b);
                            final StringBuilder sb = new StringBuilder();
                            for (Point p : b) {
                                if (last.y != p.y) {
                                    if (!vertical) {
                                        sb.append("\n");
                                    }
                                } else if (last.x != p.x - 1) {
                                    // Consider a structure like      this
                                    //                      some text here
                                    sb.append(" ");
                                }
                                last = p;
                                Tile t = board.tileAt(p.x + 1, p.y + 1);
                                sb.append(Character.toChars(t.getColor()));
                            }
                            return sb.toString();
                        })
                        .map(String::trim)
                        .filter(s -> s.length() > 0)
                        .map(s -> new ElementGameText(boardGameText, s))
                        .collect(Collectors.toList())
        );
    }

    private void dfs(Set<Point> block, int x, int y) {
        final Point p = new Point(x, y);
        if (x < 0
                || x >= board.getWidth()
                || y < 0
                || y >= board.getHeight()
                || block.contains(p)
                || !isText(board.tileAt(x + 1, y + 1))) {
            return;
        }

        block.add(p);

        // Assume we don't need to do diagonal connection
        dfs(block, x - 1, y);
        dfs(block, x + 1, y);
        dfs(block, x, y - 1);
        dfs(block, x, y + 1);
    }

    private static boolean isVertical(List<Point> points) {
        int y = points.get(0).y;
        for (int i = 1; i < points.size(); i++) {
            if (points.get(i).y != y) {
                return false;
            }
        }
        return true;
    }

    private boolean isText(Tile t) {
        return t.getType() >= game.getElements().getTextMin();
    }

    @Value
    private static class Point implements Comparable<Point> {
        int x;
        int y;

        @Override
        public int compareTo(Point o) {
            if (y != o.y) {
                return Integer.compare(y, o.y);
            }
            return Integer.compare(x, o.x);
        }
    }
}
