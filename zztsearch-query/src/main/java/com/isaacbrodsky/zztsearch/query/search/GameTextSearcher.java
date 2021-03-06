package com.isaacbrodsky.zztsearch.query.search;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.*;
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

    private final Map<String, String> firstLetterLookup;

    public GameTextSearcher(MetricRegistry metrics, ObjectMapper objectMapper, File indexDir, File catalogFile) throws IOException {
        IndexDirectories dirs = new IndexDirectories(indexDir);

        worldSearcher = new IndexSearcher(DirectoryReader.open(dirs.getWorldDirectory()));
        boardSearcher = new IndexSearcher(DirectoryReader.open(dirs.getBoardDirectory()));
        objectSearcher = new IndexSearcher(DirectoryReader.open(dirs.getObjectDirectory()));
        elementSearcher = new IndexSearcher(DirectoryReader.open(dirs.getElementDirectory()));
        combinedSearcher = new IndexSearcher(DirectoryReader.open(dirs.getCombinedDirectory()));

        parseTime = metrics.timer("searcher.parse");
        searchTime = metrics.timer("searcher.search");

        if (catalogFile.exists()) {
            log.info("Reading catalog...");
            JsonNode allDetails = objectMapper.readTree(catalogFile);
            firstLetterLookup = new HashMap<>();
            for (int i = 0; i < allDetails.size(); i++) {
                JsonNode details = allDetails.get(i);
                String zipFile = details.get("filename").asText();
                String letter = details.get("letter").asText();
                if (firstLetterLookup.containsKey(zipFile)) {
                    log.warn("Catalog contains duplicate for {}", zipFile);
                } else {
                    firstLetterLookup.put(zipFile, letter);
                }
            }
        } else {
            log.warn("Catalog missing");
            firstLetterLookup = Collections.emptyMap();
        }
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
                Collections.unmodifiableMap(contents),
                makeCanonicalUrl(contents));
    }

    private String makeCanonicalUrl(Map<String, String> fields) {
        if (!fields.containsKey("world_path")) {
            return "";
        }
        String[] parts = fields.get("world_path").split("/");
        List<String> zipFiles = Arrays.stream(parts)
                .filter(s -> s.toLowerCase().endsWith(".zip"))
                .collect(Collectors.toList());
        if (zipFiles.size() > 1) {
            // First zip file is the museum provided zip, next is the one
            // indexed in the museum.
            String zipFile = zipFiles.get(1);
            String firstLetter = firstLetterLookup.get(zipFile);
            String zztFile = parts[parts.length - 1];
            String boardIndex = fields.get("board_index");
            return "https://museumofzzt.com/file/" + firstLetter + "/" + zipFile + "?file=" + zztFile + "&board=" + boardIndex;
        } else {
            return "";
        }
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
