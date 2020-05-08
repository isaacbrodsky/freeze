package com.isaacbrodsky.zztsearch.etl.textextraction;

import com.isaacbrodsky.freeze2.game.Board;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.util.stream.Collectors;

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
        String tiles = board.tileStream()
                .map(t -> t.getType() + "," + t.getColor())
                .collect(Collectors.joining(","));
        String stats = board.getStats().stream()
                .map(s -> s.toString())
                .collect(Collectors.joining(","));
        return SHA256.digestAsHex(tiles + stats);
    }
}
