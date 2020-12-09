package com.isaacbrodsky.zztsearch.web.cli;

import com.codahale.metrics.MetricRegistry;
import com.isaacbrodsky.zztsearch.etl.WorldStreamer;
import com.isaacbrodsky.zztsearch.etl.museum.MuseumCatalogRetriever;
import com.isaacbrodsky.zztsearch.etl.text.GameText;
import com.isaacbrodsky.zztsearch.etl.textextraction.WorldTextExtractor;
import com.isaacbrodsky.zztsearch.query.indexing.GameTextIndexer;
import com.isaacbrodsky.zztsearch.web.ZZTSearchConfiguration;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.stream.Stream;

@Slf4j
public class MuseumCatalogCommand extends ConfiguredCommand<ZZTSearchConfiguration> {
    public MuseumCatalogCommand() {
        super("catalog-etl", "retrieve Museum index");
    }

    @Override
    protected void run(Bootstrap<ZZTSearchConfiguration> bootstrap, Namespace namespace, ZZTSearchConfiguration configuration) throws Exception {
        final MuseumCatalogRetriever retriever = new MuseumCatalogRetriever(bootstrap.getObjectMapper(), configuration.getMuseumUrlBase(), configuration.getMuseumFile());
        retriever.retrieve();
    }
}