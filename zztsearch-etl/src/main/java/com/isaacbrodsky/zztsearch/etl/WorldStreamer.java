package com.isaacbrodsky.zztsearch.etl;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.isaacbrodsky.freeze2.filehandling.SuperZLoader;
import com.isaacbrodsky.freeze2.filehandling.ZLoader;
import com.isaacbrodsky.freeze2.game.GameController;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.commons.io.FileUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Iterate available worlds
 */
@Slf4j
public class WorldStreamer implements Closeable {
    private static final DigestUtils SHA256 = new DigestUtils(MessageDigestAlgorithms.SHA_256);

    private final File startingDirectory;

    private final Counter skipped;
    private final Counter dirs;
    private final Counter zips;
    private final Counter zztFiles;
    private final Counter sztFiles;
    private final Counter savFiles;
    private final Counter errors;
    private final Timer fileLoadTime;

    private List<File> toCleanUp = Collections.emptyList();
    private Set<String> seenFiles = Collections.emptySet();

    public WorldStreamer(MetricRegistry metrics, File startingDirectory) {
        this.startingDirectory = startingDirectory;

        this.skipped = metrics.counter("world_streamer.skipped");
        this.dirs = metrics.counter("world_streamer.dirs");
        this.zips = metrics.counter("world_streamer.zips");
        this.zztFiles = metrics.counter("world_streamer.files.zzt");
        this.sztFiles = metrics.counter("world_streamer.files.szt");
        this.savFiles = metrics.counter("world_streamer.files.sav");
        this.errors = metrics.counter("world_streamer.errors");
        this.fileLoadTime = metrics.timer("world_streamer.file_load_time");
    }

    public Stream<World> allWorlds() throws IOException {
        close();
        return streamDir("", startingDirectory);
    }

    public Stream<World> streamDir(String path, File dir) {
        return Arrays.stream(dir.listFiles())
                .flatMap(file -> {
                    final String fileName = file.getName();
                    final String filePath = path + "/" + fileName;
                    try {
                        if (file.isDirectory()) {
                            dirs.inc();
                            return streamDir(filePath, file);
                        } else {
                            // Check if this file is already processed - e.g. due to dataset accidentally containing
                            // duplicates.
                            final String digest = SHA256.digestAsHex(file);
                            if (seenFiles.contains(digest)) {
                                skipped.inc();
                                log.trace("Skipping {}", filePath);
                                return Stream.empty();
                            } else {
                                seenFiles.add(digest);
                            }
                        }

                        if (fileName.toUpperCase().endsWith("ZIP")) {
                            ZipFile zip = new ZipFile(file);

                            File tmp = Files.createTempDirectory("zztsearch-" + fileName).toFile();
                            toCleanUp.add(tmp);

                            zip.extractAll(tmp.getAbsolutePath());

                            log.debug("Recursing into {} (on disk {})", filePath, tmp.getAbsolutePath());

                            zips.inc();
                            return streamDir(filePath, tmp);
                        }

                        try (Timer.Context ignored = fileLoadTime.time()) {
                            if (fileName.toUpperCase().endsWith("ZZT")) {
                                zztFiles.inc();
                                log.debug("Loading ZZT world {}", filePath);
                                return Stream.of(new World(filePath, fileName, ZLoader.load(new FileInputStream(file))));
                            } else if (fileName.toUpperCase().endsWith("SZT")) {
                                sztFiles.inc();
                                log.debug("Loading SuperZZT world {}", filePath);
                                return Stream.of(new World(filePath, fileName, SuperZLoader.load(new FileInputStream(file))));
                            } else if (fileName.toUpperCase().endsWith("SAV")) {
                                savFiles.inc();
                                log.debug("Loading ZZT save {}", filePath);
                                return Stream.of(new World(filePath, fileName, ZLoader.load(new FileInputStream(file))));
                            }
                        }
                    } catch (Exception e) {
                        // IOException - something messed up - whatever
                        // ZipException - bad zip file
                        // NullPointerException - occurs on corrupt file
                        //   1997.zip/paradise.zip/paradise.zzt (doesn't load in ZZT)
                        errors.inc();
                        log.warn("Failed on {}", filePath, e);
                    }
                    log.debug("Skipping {}", filePath);
                    return Stream.empty();
                });
    }

    @Override
    public void close() {
        for (File f : toCleanUp) {
            try {
                log.debug("Cleaning up {}", f.getAbsolutePath());
                FileUtils.deleteDirectory(f);
            } catch (IOException ioe) {
                log.warn("Failed to clean up", ioe);
            }
        }
        toCleanUp = new ArrayList<>();
        seenFiles = new HashSet<>();
    }

    @Value
    public class World {
        private String path;
        private String fileName;
        private GameController game;
    }
}
