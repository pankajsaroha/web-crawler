package com.github.pankajsaroha.fontier;

public interface FrontierQueue {
    void enqueue(String url);
    String take() throws InterruptedException;
    void fetchFromFile(String file);
    boolean isEmpty();
}
