package com.github.pankajsaroha.crawler;

import com.github.pankajsaroha.context.CrawlerContext;
import com.github.pankajsaroha.model.Page;
import com.github.pankajsaroha.model.Status;
import com.github.pankajsaroha.model.UrlMetadata;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class Worker implements Runnable {
    private String url;
    private CrawlerContext context;

    public Worker(String url, CrawlerContext context) {
        this.url = url;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            Page page = context.getFetcher().fetch(url);
            page.setLinks(new ArrayList<>());
            context.getParser().parse(page);

            for (var link : page.getLinks()) {
                //TODO: Add duplicate check
                context.getQueue().enqueue(link);
            }
            String filePath = context.getContentStorage().upload(page.getTitle(), page.getContent());
            UrlMetadata metadata = UrlMetadata.builder()
                    .url(url)
                    .title(page.getTitle())
                    .status(Status.DISCOVERED)
                    .fetchDuration(page.getFetchDurationMs())
                    .statusCode(page.getStatusCode())
                    .filePath(filePath)
                    .retry(0) //TODO: update it
                    .build();

            context.getMetadataStorage().insert(metadata);
        } catch (RuntimeException | IOException | InterruptedException e) {
            log.error(e.getMessage());
        }

    }
}
