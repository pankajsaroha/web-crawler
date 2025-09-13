package com.github.pankajsaroha.storage;

import com.github.pankajsaroha.model.UrlMetadata;

public interface MetadataStorage {
    void insert(UrlMetadata metadata);
}
