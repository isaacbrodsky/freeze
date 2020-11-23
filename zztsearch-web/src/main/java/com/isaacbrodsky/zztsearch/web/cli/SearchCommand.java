package com.isaacbrodsky.zztsearch.web.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isaacbrodsky.zztsearch.query.search.GameTextSearcher;
import com.isaacbrodsky.zztsearch.query.search.Index;
import com.isaacbrodsky.zztsearch.web.ZZTSearchConfiguration;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

@Slf4j
public class SearchCommand extends ConfiguredCommand<ZZTSearchConfiguration> {
    public SearchCommand() {
        super("search", "command line search");
    }

    @Override
    public void configure(Subparser subparser) {
        // Add a command line option
        subparser.addArgument("-i", "--index")
                .dest("index")
                .type(Index.class)
                .required(false)
                .setDefault(Index.COMBINED)
                .help("Index (world, board, object, element, combined)");
        subparser.addArgument("-f", "--field")
                .dest("field")
                .type(String.class)
                .required(false)
                .setDefault("combined")
                .help("Field (or combined)");
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
        final Index index = namespace.get("index");
        final String field = namespace.getString("field");
        final int num = namespace.getInt("num");
        final ObjectMapper objectMapper = bootstrap.getObjectMapper();

        try (final Scanner scanner = new Scanner(new InputStreamReader(System.in))) {
            final GameTextSearcher searcher = new GameTextSearcher(bootstrap.getMetricRegistry(), configuration.indexDirectory);

            String line;
            while ((line = scanner.nextLine()) != null) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(System.out, searcher.search(index, field, line, num));
            }
        } catch (Exception ex) {
            log.error("Failed to search", ex);
            throw new RuntimeException(ex);
        }
    }
}