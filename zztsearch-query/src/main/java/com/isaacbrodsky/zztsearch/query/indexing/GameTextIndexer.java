package com.isaacbrodsky.zztsearch.query.indexing;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.isaacbrodsky.zztsearch.etl.text.GameText;
import com.isaacbrodsky.zztsearch.query.IndexDirectories;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

@Slf4j
public class GameTextIndexer {
    private final File indexDirectory;

    private final Counter worldTexts;
    private final Counter boardTexts;
    private final Counter elementTexts;
    private final Counter objectTexts;
    private final Timer indexTime;
    private final Timer mergeTime;
    private final Timer overallTime;

    public GameTextIndexer(MetricRegistry metrics, File indexDirectory) {
        this.indexDirectory = indexDirectory;

        this.worldTexts = metrics.counter("indexing.texts.world");
        this.boardTexts = metrics.counter("indexing.texts.board");
        this.elementTexts = metrics.counter("indexing.texts.element");
        this.objectTexts = metrics.counter("indexing.texts.object");
        this.indexTime = metrics.timer("indexing.time.index");
        this.mergeTime = metrics.timer("indexing.time.merge");
        this.overallTime = metrics.timer("indexing.time.overall");
    }

    public void build(Stream<GameText> text) throws IOException {
        try (Timer.Context ignored = overallTime.time()) {

            final IndexDirectories dirs = new IndexDirectories(indexDirectory);
            try (final IndexWriter worldWriter = buildWriter(dirs.getWorldDirectory());
                 final IndexWriter boardWriter = buildWriter(dirs.getBoardDirectory());
                 final IndexWriter elementWriter = buildWriter(dirs.getElementDirectory());
                 final IndexWriter objectWriter = buildWriter(dirs.getObjectDirectory());
                 final IndexWriter combinedWriter = buildWriter(dirs.getCombinedDirectory())) {
                text
                        .map(t -> {
                            final Sorter sorter = new Sorter(worldWriter, boardWriter, elementWriter, objectWriter);
                            t.accept(sorter);
                            return new SortedText(t, sorter.getActualWriter());
                        })
                        .map(t -> {
                            final Formatter formatter = new Formatter();
                            t.getText().accept(formatter);
                            Formatter.addCombined(formatter.getDoc());
                            return new TargetedDoc(formatter.getDoc(), t.getWriter());
                        })
                        .forEach(t -> {
                            try (Timer.Context ignored2 = indexTime.time()) {
                                t.getWriter().addDocument(t.getDoc());
                                // TODO may be better to combine a single per-board document?
                                combinedWriter.addDocument(t.getDoc());
                            } catch (IOException ioe) {
                                log.error("Failed to index", ioe);
                            }
                        });

                forceMerge("worlds", worldWriter);
                forceMerge("boards", boardWriter);
                forceMerge("elements", elementWriter);
                forceMerge("objects", objectWriter);
                forceMerge("combined", combinedWriter);
            }
        }
        log.info("All indexing done");
    }

    private IndexWriter buildWriter(Directory dir) throws IOException {
        final Analyzer analyzer = new StandardAnalyzer();
        final IndexWriterConfig iwc = new IndexWriterConfig(analyzer)
                // Do not update - only create
                .setOpenMode(IndexWriterConfig.OpenMode.CREATE)
                .setRAMBufferSizeMB(256.0);

        return new IndexWriter(dir, iwc);
    }

    private void forceMerge(String name, IndexWriter writer) throws IOException {
        log.info("Forcing merge {}", name);
        try (Timer.Context ignored2 = mergeTime.time()) {
            writer.forceMerge(1);
        }
    }

    @Value
    private static class SortedText {
        GameText text;
        IndexWriter writer;
    }

    @Value
    private static class TargetedDoc {
        Document doc;
        IndexWriter writer;
    }
}
