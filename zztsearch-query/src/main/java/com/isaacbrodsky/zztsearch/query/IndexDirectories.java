package com.isaacbrodsky.zztsearch.query;

import lombok.Value;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Value
public class IndexDirectories {
    private Directory worldDirectory;
    private Directory boardDirectory;
    private Directory elementDirectory;
    private Directory objectDirectory;
    private Directory combinedDirectory;

    public IndexDirectories(File indexDirectory) throws IOException  {
        final Path root = indexDirectory.toPath();
        this.worldDirectory = new SimpleFSDirectory(root.resolve("worlds"));
        this.boardDirectory = new SimpleFSDirectory(root.resolve("boards"));
        this.elementDirectory = new SimpleFSDirectory(root.resolve("elements"));
        this.objectDirectory = new SimpleFSDirectory(root.resolve("objects"));
        this.combinedDirectory = new SimpleFSDirectory(root.resolve("combined"));
    }
}
