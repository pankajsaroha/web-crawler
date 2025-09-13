package com.github.pankajsaroha.crawler;

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
    private InMemoryFrontierQueue frontierQueue;
    private HttpClient client;
    private ExecutorService executorService;

    public Crawler(InMemoryFrontierQueue frontierQueue) {
        this.frontierQueue = frontierQueue;
        this.client = HttpClient.newHttpClient();
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public void startCrawling() throws InterruptedException {
        String url = frontierQueue.take();
        //executorService.submit(new Worker(url));
    }
}
