package com.isaacbrodsky.zztsearch.web;

import com.isaacbrodsky.zztsearch.etl.WorldStreamer;
import com.isaacbrodsky.zztsearch.query.search.GameTextSearcher;
import com.isaacbrodsky.zztsearch.web.cli.IndexCommand;
import com.isaacbrodsky.zztsearch.web.cli.MuseumCatalogCommand;
import com.isaacbrodsky.zztsearch.web.cli.SearchCommand;
import com.isaacbrodsky.zztsearch.web.cli.TopBoardsCommand;
import com.isaacbrodsky.zztsearch.web.resources.SearchResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

@Slf4j
public class ZZTSearchApplication extends Application<ZZTSearchConfiguration> {

    public static void main(final String[] args) throws Exception {
        new ZZTSearchApplication().run(args);
    }

    @Override
    public String getName() {
        return "ZZTSearch";
    }

    @Override
    public void initialize(final Bootstrap<ZZTSearchConfiguration> bootstrap) {
        bootstrap.addCommand(new IndexCommand());
        bootstrap.addCommand(new SearchCommand());
        bootstrap.addCommand(new TopBoardsCommand());
        bootstrap.addCommand(new MuseumCatalogCommand());
    }

    @Override
    public void run(final ZZTSearchConfiguration configuration,
                    final Environment environment) {
        try {
            GameTextSearcher searcher = new GameTextSearcher(environment.metrics(),
                    environment.getObjectMapper(),
                    configuration.indexDirectory,
                    configuration.getMuseumFile());
            SearchResource searchResource = new SearchResource(searcher);
            environment.jersey().register(searchResource);
        } catch (Exception ex) {
            log.error("Failed to start up", ex);
            throw new RuntimeException(ex);
        }
    }

}
