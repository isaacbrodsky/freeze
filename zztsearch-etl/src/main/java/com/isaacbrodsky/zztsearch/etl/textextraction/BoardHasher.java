package com.isaacbrodsky.zztsearch.etl.textextraction;

import com.isaacbrodsky.freeze.elements.Element;
import com.isaacbrodsky.freeze.game.Board;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

/**
 * Take a board and produce a hash of the elements on it.
 */
public class BoardHasher {
    private static final DigestUtils SHA256 = new DigestUtils(MessageDigestAlgorithms.SHA_256);

    private final Board board;

    public BoardHasher(Board board) {
        this.board = board;
    }

    public String compute() {
        final Element[][][] elements = board.getElements();
        final StringBuilder sb = new StringBuilder();
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                for (int d = 0; d < board.getDepth(); d++) {
                    final Element e = elements[x][y][d];
                    sb.append(',');
                    if (e == null) {
                        sb.append("null");
                    } else {
                        // Using only the visual appearance, but also
                        // using depth...
                        sb.append(e.getDisplayCharacter())
                                .append(';')
                                .append(e.getColoring().getCode());
                    }
                }
            }
        }

        return SHA256.digestAsHex(sb.toString());
    }
}
