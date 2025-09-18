package com.github.pankajsaroha.fontier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class InMemoryFrontierQueue implements FrontierQueue {
    private BlockingQueue<String> queue;

    public InMemoryFrontierQueue() {
        this.queue = new LinkedBlockingQueue<>();
    }

    public void enqueue (String url) {
        queue.offer(url);
    }

    public int fetchFromFile(String fileLocation) {
        int urls = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileLocation))) {
            String url;
            while ((url = reader.readLine()) != null) {
                queue.offer(url);
                urls++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return urls;
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public String take() throws InterruptedException {
        return queue.take();
    }
}
