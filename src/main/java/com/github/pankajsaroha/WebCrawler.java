package com.github.pankajsaroha;

import com.github.pankajsaroha.crawler.Crawler;
import com.github.pankajsaroha.fontier.InMemoryFrontierQueue;

import java.io.IOException;

public class WebCrawler {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello world!");
        InMemoryFrontierQueue queue = new InMemoryFrontierQueue();
        queue.enqueue("https://example.com/");

        Crawler crawler = new Crawler(queue);
        crawler.parse("https://example.com/");
    }
}