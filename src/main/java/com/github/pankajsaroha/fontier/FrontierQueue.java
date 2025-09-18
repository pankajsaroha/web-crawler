package com.github.pankajsaroha.fontier;

public interface FrontierQueue {
    void enqueue(String url);
    String take() throws InterruptedException;
    int fetchFromFile(String file);
    boolean isEmpty();
}
