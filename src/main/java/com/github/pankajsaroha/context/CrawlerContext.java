package com.github.pankajsaroha.context;

import com.github.pankajsaroha.crawler.Fetcher;
import com.github.pankajsaroha.crawler.Parser;
import com.github.pankajsaroha.filter.BloomFilter;
import com.github.pankajsaroha.fontier.FrontierQueue;
import com.github.pankajsaroha.storage.ContentStorage;
import com.github.pankajsaroha.storage.MetadataStorage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class CrawlerContext {
    private FrontierQueue queue;
    private Fetcher fetcher;
    private Parser parser;
    private ContentStorage contentStorage;
    private MetadataStorage metadataStorage;
    private BloomFilter filter;
    private int depth;
    private AtomicInteger processedUrls;

    public CrawlerContext(FrontierQueue queue, Fetcher fetcher, Parser parser, ContentStorage contentStorage, MetadataStorage metadataStorage, BloomFilter filter, int depth) {
        this.queue = queue;
        this.fetcher = fetcher;
        this.parser = parser;
        this.contentStorage = contentStorage;
        this.metadataStorage = metadataStorage;
        this.filter = filter;
        this.depth = depth;
        this.processedUrls = new AtomicInteger();
    }

    public void incrementProcessedUrls() {
        processedUrls.incrementAndGet();
    }

    public int getProcessedUrls() {
        return processedUrls.get();
    }
}
