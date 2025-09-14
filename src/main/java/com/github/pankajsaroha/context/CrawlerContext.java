package com.github.pankajsaroha.context;

import com.github.pankajsaroha.crawler.Fetcher;
import com.github.pankajsaroha.crawler.Parser;
import com.github.pankajsaroha.fontier.FrontierQueue;
import com.github.pankajsaroha.storage.ContentStorage;
import com.github.pankajsaroha.storage.MetadataStorage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CrawlerContext {
    private FrontierQueue queue;
    private Fetcher fetcher;
    private Parser parser;
    private ContentStorage contentStorage;
    private MetadataStorage metadataStorage;
}
