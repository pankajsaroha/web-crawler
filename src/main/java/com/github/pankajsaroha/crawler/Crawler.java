package com.github.pankajsaroha.crawler;

import com.github.pankajsaroha.context.CrawlerContext;
import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Crawler {
    private HttpClient client;
    private CrawlerContext context;
    private ExecutorService executorService;
    private volatile boolean running;
    private int numThreads;
    private boolean useVirtualThreads;

    public Crawler(CrawlerContext context, int numThreads, boolean useVirtualThreads) {
        this.context = context;
        this.client = HttpClient.newHttpClient();
        this.running = true;
        this.numThreads = numThreads;
        this.useVirtualThreads = useVirtualThreads;
        this.executorService = useVirtualThreads ? Executors.newVirtualThreadPerTaskExecutor()
        :Executors.newFixedThreadPool(numThreads);
    }

    public void startCrawling() throws InterruptedException {
        if (useVirtualThreads) {
            // With virtual threads, just spawn a task per URL
            executorService.submit(() -> {
                while (running) {
                    try {
                        String url = context.getQueue().take();
                        executorService.submit(new Worker(url, context));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        } else {
            for (var i = 0; i < numThreads; i++) {
                executorService.submit(() -> {
                    while (running && !Thread.currentThread().isInterrupted()) {
                        try {
                            String url = context.getQueue().take();
                            new Worker(url, context).run();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                });
            }
        }
    }

    public void shutdown() {
        running = false;
        executorService.shutdown(); //graceful stop

        //TODO: TimeUnit not working, maybe indexing issue
        //some threads might already be in while loop, but queue is blocked as no url is available
        //they won't shutdown. We need to wait before shutting them down forcefully
        /*if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
            executorService.shutdownNow(); // force stop if not finished
        }*/
    }
}
