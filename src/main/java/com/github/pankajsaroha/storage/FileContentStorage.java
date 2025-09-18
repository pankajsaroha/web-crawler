package com.github.pankajsaroha.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileContentStorage implements ContentStorage {
    private final Path basedir;
    private final static AtomicBoolean initialized = new AtomicBoolean(false);

    public FileContentStorage() {
        try {
            basedir = Path.of("content");
            Files.createDirectories(basedir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Considering each page have a unique title
     * @param title
     * @param content
     * @return
     */
    @Override
    public String upload(String title, String content) {
        try {
            Path contentPath = basedir.resolve(title);
            Files.writeString(contentPath, content,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            return contentPath.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
