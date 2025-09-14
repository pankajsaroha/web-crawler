package com.github.pankajsaroha.storage;

import com.github.pankajsaroha.model.UrlMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class UrlMetadataStorage implements MetadataStorage {

    @Override
    public void insert(UrlMetadata metadata) {
        try {
            Path basedir = Path.of("metadata");
            Files.createDirectories(basedir);
            Path filePath = basedir.resolve("metadata.txt");
            Files.writeString(filePath, metadata.toString(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
