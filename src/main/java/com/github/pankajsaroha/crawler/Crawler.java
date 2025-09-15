package com.github.pankajsaroha.crawler;

import com.github.pankajsaroha.context.CrawlerContext;
import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Crawler {
    public enum Mode {
        FIXED_PLATFORM_POOL,  // N platform threads
        PLATFORM_PER_TASK,   // one platform thread per task
        VIRTUAL_PER_TASK    // one virtual thread per task
    }
    private HttpClient client;
    private CrawlerContext context;
    private ExecutorService executorService;
    private volatile boolean running;
    private int numThreads;
    private Mode mode;

    public Crawler(CrawlerContext context, int numThreads, Mode mode) {
        this.context = context;
        this.client = HttpClient.newHttpClient();
        this.running = true;
        this.numThreads = numThreads;
        this.mode = mode;
        //This will spawn as number of virtual threads as tasks but fixed platform threads.
        //this.executorService = useVirtualThreads ? Executors.newVirtualThreadPerTaskExecutor() : Executors.newFixedThreadPool(numThreads);
        this.executorService = switch (mode) {
            case FIXED_PLATFORM_POOL -> Executors.newFixedThreadPool(numThreads);
            case PLATFORM_PER_TASK -> Executors.newThreadPerTaskExecutor(Thread.ofPlatform().factory());
            case VIRTUAL_PER_TASK -> Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());
        };
    }

    /**
     * We should never call .run() directly if you want the task to be executed by the executor’s threads — calling .run() just executes
     * the code synchronously in the current thread, bypassing the executor entirely.
     *
     * new Worker(url, context).run();
     * This runs the worker in the same thread that’s running workerLoop().
     * So in FIXED_PLATFORM_POOL mode, that’s fine — because we want bounded concurrency (same thread which polls the url, executes it) and the loop itself is running inside the pool threads.
     * But in PLATFORM_PER_TASK and VIRTUAL_PER_TASK modes, we’re already creating a new task per URL in startCrawling() so that executor service could map it to appropriate thread
     * @throws InterruptedException
     */
    public void startCrawling() throws InterruptedException {
        switch (mode) {
            case FIXED_PLATFORM_POOL -> {
                //bounded concurrency — loop inside pool threads:
                for (int i = 0; i < numThreads; i++) {
                    executorService.submit(this::workerLoop);
                }
            }
            case PLATFORM_PER_TASK, VIRTUAL_PER_TASK -> {
                //Spawn a new thread for each URL
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
            }
        }
    }

    private void workerLoop() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                String url = context.getQueue().take();
                new Worker(url, context).run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
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
