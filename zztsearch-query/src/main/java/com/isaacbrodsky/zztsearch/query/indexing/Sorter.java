package com.isaacbrodsky.zztsearch.query.indexing;

import com.isaacbrodsky.zztsearch.etl.text.BoardGameText;
import com.isaacbrodsky.zztsearch.etl.text.ElementGameText;
import com.isaacbrodsky.zztsearch.etl.text.GameTextVisitor;
import com.isaacbrodsky.zztsearch.etl.text.ObjectGameText;
import com.isaacbrodsky.zztsearch.etl.text.WorldGameText;
import lombok.Getter;
import org.apache.lucene.index.IndexWriter;

/**
 * NOT thread safe
 *
 * Sorts a GameText to its appropriate index.
 */
class Sorter implements GameTextVisitor {
    private final IndexWriter worldWriter;
    private final IndexWriter boardWriter;
    private final IndexWriter elementWriter;
    private final IndexWriter objectWriter;

    public Sorter(IndexWriter worldWriter,
                  IndexWriter boardWriter,
                  IndexWriter elementWriter,
                  IndexWriter objectWriter) {
        this.worldWriter = worldWriter;
        this.boardWriter = boardWriter;
        this.elementWriter = elementWriter;
        this.objectWriter = objectWriter;
    }

    @Getter
    private IndexWriter actualWriter;

    @Override
    public void visit(ElementGameText element) {
        this.actualWriter = elementWriter;
    }

    @Override
    public void visit(ObjectGameText object) {
        this.actualWriter = objectWriter;
    }

    @Override
    public void visit(WorldGameText world) {
        this.actualWriter = worldWriter;
    }

    @Override
    public void visit(BoardGameText board) {
        this.actualWriter = boardWriter;
    }
}
