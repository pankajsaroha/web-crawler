package com.github.pankajsaroha;

import com.github.pankajsaroha.context.CrawlerContext;
import com.github.pankajsaroha.crawler.Crawler;
import com.github.pankajsaroha.crawler.Fetcher;
import com.github.pankajsaroha.crawler.Parser;
import com.github.pankajsaroha.fontier.InMemoryFrontierQueue;
import com.github.pankajsaroha.storage.FileContentStorage;
import com.github.pankajsaroha.storage.UrlMetadataStorage;


import java.io.IOException;

public class WebCrawler {
    public static void main(String[] args) throws IOException, InterruptedException {
        InMemoryFrontierQueue queue = new InMemoryFrontierQueue();
        queue.enqueue("https://example.com/");

        Crawler crawler = new Crawler(new CrawlerContext(queue, new Fetcher(), new Parser(), new FileContentStorage(), new UrlMetadataStorage()), 10, Crawler.Mode.VIRTUAL_PER_TASK);
        crawler.startCrawling();


    }
}