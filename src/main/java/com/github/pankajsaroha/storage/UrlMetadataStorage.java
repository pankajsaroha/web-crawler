package com.github.pankajsaroha.storage;

import com.github.pankajsaroha.model.UrlMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicBoolean;

public class UrlMetadataStorage implements MetadataStorage {
    private Path filePath;
    private final static AtomicBoolean initialized = new AtomicBoolean(false);

    public UrlMetadataStorage() {
        try {
            Path basedir = Path.of("metadata");
            Files.createDirectories(basedir);
            filePath = basedir.resolve("metadata.txt");
            //if file already exists, clean the file first so that it does not append to the old data
            if (initialized.compareAndSet(false, true)) {
                Files.writeString(filePath, "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(UrlMetadata metadata) {
        try {
            Files.writeString(filePath, metadata.toString(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
