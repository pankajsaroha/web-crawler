package com.github.pankajsaroha.crawler;

import com.github.pankajsaroha.context.CrawlerContext;
import com.github.pankajsaroha.fontier.InMemoryFrontierQueue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Crawler {
    private HttpClient client;
    private CrawlerContext context;
    private ExecutorService executorService;

    public Crawler(CrawlerContext context) {
        this.context = context;
        this.client = HttpClient.newHttpClient();
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public void startCrawling() throws InterruptedException {
        String url = context.getQueue().take();
        executorService.submit(new Worker(url, context));
    }
}
