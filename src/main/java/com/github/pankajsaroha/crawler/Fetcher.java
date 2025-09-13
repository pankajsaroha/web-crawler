package com.github.pankajsaroha.crawler;

import com.github.pankajsaroha.model.Page;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class Fetcher {
    private HttpClient client;

    public Fetcher() {
        this.client = HttpClient.newHttpClient();
    }

    public Page fetch(String url) throws IOException, InterruptedException {
        long fetchRequestStart = System.currentTimeMillis();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response == null) {
            throw new RuntimeException("No content found for the url: " + url);
        }
        if (response.statusCode() != 200) {
            throw new RuntimeException("Page failed to fetch with status: " + response.statusCode());
        }
        return Page.builder()
                .content(response.body())
                .statusCode(response.statusCode())
                .fetchDurationMs(System.currentTimeMillis() - fetchRequestStart)
                .build();
    }
}
