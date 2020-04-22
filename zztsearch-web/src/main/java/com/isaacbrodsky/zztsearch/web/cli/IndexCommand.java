package com.isaacbrodsky.zztsearch.web.cli;

import com.codahale.metrics.MetricRegistry;
import com.isaacbrodsky.zztsearch.etl.WorldStreamer;
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
public class IndexCommand extends ConfiguredCommand<ZZTSearchConfiguration> {
    public IndexCommand() {
        super("index", "build index files");
    }

    @Override
    protected void run(Bootstrap<ZZTSearchConfiguration> bootstrap, Namespace namespace, ZZTSearchConfiguration configuration) {
        final MetricRegistry metrics = bootstrap.getMetricRegistry();
        try (final WorldStreamer worlds = new WorldStreamer(metrics, configuration.getWorldDirectory())) {
            final GameTextIndexer indexer = new GameTextIndexer(metrics, configuration.getIndexDirectory());
            final Stream<GameText> extracted = worlds.allWorlds()
                    .flatMap(w -> new WorldTextExtractor(w.getPath(), w.getFileName(), w.getGame()).allText().stream());
            indexer.build(extracted);
        } catch (Exception ex) {
            log.error("Error indexing worlds", ex);
        }
    }
}