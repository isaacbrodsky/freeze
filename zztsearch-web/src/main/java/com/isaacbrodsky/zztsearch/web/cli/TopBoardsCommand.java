package com.isaacbrodsky.zztsearch.web.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isaacbrodsky.zztsearch.query.search.GameTextSearcher;
import com.isaacbrodsky.zztsearch.query.search.Index;
import com.isaacbrodsky.zztsearch.web.ZZTSearchConfiguration;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Scorer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

@Slf4j
public class TopBoardsCommand extends ConfiguredCommand<ZZTSearchConfiguration> {
    public TopBoardsCommand() {
        super("topboards", "aggregate top boards");
    }

    @Override
    public void configure(Subparser subparser) {
        subparser.addArgument("-n", "--num")
                .dest("num")
                .type(Integer.class)
                .required(false)
                .setDefault(10)
                .help("Number of results");
        addFileArgument(subparser);
    }

    @Override
    protected void run(Bootstrap<ZZTSearchConfiguration> bootstrap, Namespace namespace, ZZTSearchConfiguration configuration) {
        final int num = namespace.getInt("num");
        final ObjectMapper objectMapper = bootstrap.getObjectMapper();

        try {
            final GameTextSearcher searcher = new GameTextSearcher(bootstrap.getMetricRegistry(), objectMapper, configuration.indexDirectory, configuration.getMuseumFile());

            final Map<String, TopBoardResult> counts = new HashMap<>();
            final IndexSearcher boards = searcher.determineSearcher(Index.BOARD);

            final LeafCollector leafCollector = new LeafCollector() {
                @Override
                public void setScorer(Scorer scorer) throws IOException {

                }

                @Override
                public void collect(int i) throws IOException {
                    final Document d = boards.doc(i);
                    final String title = d.getField("board_title").stringValue();
                    final String hash = d.getField("board_visualhash").stringValue();

                    final TopBoardResult result = counts.getOrDefault(hash, new TopBoardResult(hash, title, 0));
                    counts.put(hash, result.inc());
                }
            };

            Collector collector = new Collector() {
                @Override
                public LeafCollector getLeafCollector(LeafReaderContext leafReaderContext) throws IOException {
                    return leafCollector;
                }

                @Override
                public boolean needsScores() {
                    return false;
                }
            };

            boards.search(new MatchAllDocsQuery(), collector);

            objectMapper.writeValue(System.out, counts.values()
                    .stream()
                    .sorted(Comparator.reverseOrder())
                    .limit(num)
                    .collect(Collectors.toList()));
        } catch (Exception ex) {
            log.error("Failed to search", ex);
            throw new RuntimeException(ex);
        }
    }

    @Value
    private static class TopBoardResult implements Comparable<TopBoardResult> {
        String hash;
        // One title of this board
        String title;
        int count;

        public TopBoardResult inc() {
            return new TopBoardResult(hash, title, count + 1);
        }

        @Override
        public int compareTo(TopBoardResult o) {
            return Integer.compare(count, o.count);
        }
    }
}