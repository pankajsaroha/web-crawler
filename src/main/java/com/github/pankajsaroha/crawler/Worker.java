package com.github.pankajsaroha.crawler;

import com.github.pankajsaroha.fontier.FrontierQueue;
import com.github.pankajsaroha.model.Page;
import com.github.pankajsaroha.model.Status;
import com.github.pankajsaroha.model.UrlMetadata;
import com.github.pankajsaroha.storage.ContentStorage;
import com.github.pankajsaroha.storage.MetadataStorage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Worker implements Runnable {
    private String url;
    private FrontierQueue queue;
    private Fetcher fetcher;
    private Parser parser;
    private ContentStorage contentStorage;
    private MetadataStorage metadataStorage;

    public Worker(String url, FrontierQueue queue, Fetcher fetcher, Parser parser, ContentStorage contentStorage, MetadataStorage metadataStorage) {
        this.url = url;
        this.queue = queue;
        this.fetcher = fetcher;
        this.parser = parser;
        this.contentStorage = contentStorage;
        this.metadataStorage = metadataStorage;
    }

    @Override
    public void run() {
        try {
            Page page = fetcher.fetch(url);
            parser.parse(page);

            for (var link : page.getLinks()) {
                //TODO: Add duplicate check
                queue.enqueue(link);
            }
            String filePath = contentStorage.upload(page.getContent());
            UrlMetadata metadata = UrlMetadata.builder()
                    .url(url)
                    .title(page.getTitle())
                    .status(Status.DISCOVERED)
                    .fetchDuration(page.getFetchDurationMs())
                    .statusCode(page.getStatusCode())
                    .filePath(filePath)
                    .retry(0) //TODO: update it
                    .build();

            metadataStorage.insert(metadata);
        } catch (RuntimeException | IOException | InterruptedException e) {
            log.error(e.getMessage());
        }

    }
}
