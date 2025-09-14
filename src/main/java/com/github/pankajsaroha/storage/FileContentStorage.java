package com.github.pankajsaroha.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileContentStorage implements ContentStorage {
    @Override
    public String upload(String title, String content) {
        try {
            Path basedir = Path.of("content");
            Files.createDirectories(basedir);
            Path contentPath = basedir.resolve(title);
            Files.writeString(contentPath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return contentPath.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
