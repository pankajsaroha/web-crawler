package com.github.pankajsaroha;

import com.github.pankajsaroha.context.CrawlerContext;
import com.github.pankajsaroha.crawler.Crawler;
import com.github.pankajsaroha.crawler.Fetcher;
import com.github.pankajsaroha.crawler.Parser;
import com.github.pankajsaroha.fontier.FrontierQueue;
import com.github.pankajsaroha.fontier.InMemoryFrontierQueue;
import com.github.pankajsaroha.storage.FileContentStorage;
import com.github.pankajsaroha.storage.UrlMetadataStorage;


import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class WebCrawler {
    public static void main(String[] args) throws IOException, InterruptedException {
        FrontierQueue queue = new InMemoryFrontierQueue();
        //queue.enqueue("https://example.com/");
        Path basedir = Path.of("seed-urls");
        queue.fetchFromFile(basedir.resolve("urls.txt").toString());

        List<BenchmarkResult> results = new ArrayList<>();

        for (Crawler.Mode mode : Crawler.Mode.values()) {
            System.out.println("Running Benchmark for mode: " + mode);
            BenchmarkResult result = runBenchmark(mode, queue);
            results.add(result);
        }

        Path benchmarkResults = Path.of("benchmark-results");
        Files.createDirectories(benchmarkResults);
        writeToCsv(results, basedir.resolve("benchmark-results.csv"));
        System.out.println("Benchmark complete. Results written to: " + benchmarkResults.toAbsolutePath());
        /*Crawler crawler = new Crawler(new CrawlerContext(queue, new Fetcher(), new Parser(), new FileContentStorage(), new UrlMetadataStorage()), 10, Crawler.Mode.VIRTUAL_PER_TASK);
        crawler.startCrawling();
        crawler.shutdown();*/
    }

    private static BenchmarkResult runBenchmark(Crawler.Mode mode, FrontierQueue queue) throws InterruptedException {
        Crawler crawler = new Crawler(new CrawlerContext(queue, new Fetcher(), new Parser(), new FileContentStorage(), new UrlMetadataStorage()), 10, mode);

        Instant startTime = Instant.now();
        crawler.startCrawling();

        //wait until all URLs are processed
        while (!queue.isEmpty()) {
            Thread.sleep(100);
        }
        crawler.shutdown();
        return null;
    }

    private static class BenchmarkResult {
        String mode;
        int totalProcessed;
        int errorCount;
        long totalTimeMillis;
        double throughput;
        long memoryUsedBytes;
        int peakThreadCount;
        double cpuLoad;

        public BenchmarkResult(String mode, int totalProcessed, int errorCount, long totalTimeMillis, double throughput, long memoryUsedBytes, int peakThreadCount, double cpuLoad) {
            this.mode = mode;
            this.totalProcessed = totalProcessed;
            this.errorCount = errorCount;
            this.totalTimeMillis = totalTimeMillis;
            this.throughput = throughput;
            this.memoryUsedBytes = memoryUsedBytes;
            this.peakThreadCount = peakThreadCount;
            this.cpuLoad = cpuLoad;
        }

        @Override
        public String toString() {
            return "BenchmarkResult{" +
                    "mode='" + mode + '\'' +
                    ", totalProcessed=" + totalProcessed +
                    ", errorCount=" + errorCount +
                    ", totalTimeMillis=" + totalTimeMillis +
                    ", throughput=" + throughput +
                    ", memoryUsedBytes=" + memoryUsedBytes +
                    ", peakThreadCount=" + peakThreadCount +
                    ", cpuLoad=" + cpuLoad +
                    '}';
        }
    }

    private static void writeToCsv(List<BenchmarkResult> results, Path path) throws IOException {
        try(PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path))) {
            writer.println("Mode    |   URL Processed   |   TotalTimeMillis     |   Throughput(URLs/sec)");
            for (BenchmarkResult result : results) {
                writer.printf("%s   |   %d  |   %d  |   %.2f\n", result.mode, result.totalProcessed, result.totalTimeMillis, result.throughput);
            }
        }
    }
}