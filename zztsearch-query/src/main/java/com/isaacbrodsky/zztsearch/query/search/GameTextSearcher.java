package com.isaacbrodsky.zztsearch.query.search;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.isaacbrodsky.zztsearch.query.IndexDirectories;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class GameTextSearcher {
    private final IndexSearcher worldSearcher;
    private final IndexSearcher boardSearcher;
    private final IndexSearcher objectSearcher;
    private final IndexSearcher elementSearcher;
    private final IndexSearcher combinedSearcher;

    private final Timer parseTime;
    private final Timer searchTime;

    public GameTextSearcher(MetricRegistry metrics, File indexDir) throws IOException {
        IndexDirectories dirs = new IndexDirectories(indexDir);

        worldSearcher = new IndexSearcher(DirectoryReader.open(dirs.getWorldDirectory()));
        boardSearcher = new IndexSearcher(DirectoryReader.open(dirs.getBoardDirectory()));
        objectSearcher = new IndexSearcher(DirectoryReader.open(dirs.getObjectDirectory()));
        elementSearcher = new IndexSearcher(DirectoryReader.open(dirs.getElementDirectory()));
        combinedSearcher = new IndexSearcher(DirectoryReader.open(dirs.getCombinedDirectory()));

        parseTime = metrics.timer("searcher.parse");
        searchTime = metrics.timer("searcher.search");
    }

    public SearchResult search(Index index, String field, String queryText) throws IOException, ParseException {
        return search(index, field, queryText, 10);
    }

    public SearchResult search(Index index, String field, String queryText, int n) throws IOException, ParseException {
        final Query query;
        final IndexSearcher searcher;
        try (Timer.Context ignored = parseTime.time()) {
            final Analyzer analyzer = new StandardAnalyzer();
            final QueryParser parser = new QueryParser(field, analyzer);
            query = parser.parse(queryText);
            log.debug("Searching for {}", query.toString());
            searcher = determineSearcher(index);
        }
        final TopDocs top;
        try (Timer.Context ignored = searchTime.time()) {
            top = searcher.search(query, n);
        }
        return reformatResults(searcher, top);
    }

    private SearchResult reformatResults(IndexSearcher searcher, TopDocs top) {
        return new SearchResult(top.totalHits,
                top.getMaxScore(),
                Collections.unmodifiableList(
                        Arrays.stream(top.scoreDocs)
                                .map(d -> reformatDoc(searcher, d))
                                .collect(Collectors.toList())));
    }

    private SearchResult.Doc reformatDoc(IndexSearcher searcher, ScoreDoc docId) {
        final Document doc;
        try {
            doc = searcher.doc(docId.doc);
        } catch (IOException ioe) {
            throw new RuntimeException("Failed to retrieve document", ioe);
        }

        final Map<String, String> contents = doc.getFields().stream()
                .collect(Collectors.toMap(IndexableField::name, IndexableField::stringValue));

        return new SearchResult.Doc(docId.doc,
                docId.score,
                docId.shardIndex,
                Collections.unmodifiableMap(contents));
    }

    // Probably should not be part of public API of this class
    public IndexSearcher determineSearcher(Index index) {
        switch (index) {
            case WORLD: return worldSearcher;
            case BOARD: return boardSearcher;
            case OBJECT: return objectSearcher;
            case ELEMENT: return elementSearcher;
            case COMBINED: return combinedSearcher;
            default: throw new IllegalArgumentException("Invalid index: " + index);
        }
    }
}
