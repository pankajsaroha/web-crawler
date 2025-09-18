package com.github.pankajsaroha;

import com.github.pankajsaroha.context.CrawlerContext;
import com.github.pankajsaroha.crawler.Crawler;
import com.github.pankajsaroha.crawler.Fetcher;
import com.github.pankajsaroha.crawler.Parser;
import com.github.pankajsaroha.filter.BloomFilter;
import com.github.pankajsaroha.fontier.FrontierQueue;
import com.github.pankajsaroha.fontier.InMemoryFrontierQueue;
import com.github.pankajsaroha.storage.FileContentStorage;
import com.github.pankajsaroha.storage.UrlMetadataStorage;
import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WebCrawler {
    public static void main(String[] args) throws IOException, InterruptedException {
        FrontierQueue queue = new InMemoryFrontierQueue();

        Path basedir = Path.of("seed-urls");
        int numberOfUrls = queue.fetchFromFile(basedir.resolve("urls.txt").toString());

        List<BenchmarkResult> results = new ArrayList<>();

        //TODO: Move to config file
        int numThreads = 10; // number of fixed threads
        int depth = 2; // number of child urls to explore

        for (Crawler.Mode mode : Crawler.Mode.values()) {
            System.out.println("================== Running Benchmark for mode: " + mode + " ========================");
            BenchmarkResult result = runBenchmark(mode, queue, numThreads, numberOfUrls, depth);
            results.add(result);
        }

        Path benchmarkResults = Path.of("benchmark-results");
        Files.createDirectories(benchmarkResults);
        Files.writeString(benchmarkResults.resolve("benchmark-results.csv"), "",
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        writeToCsv(results, benchmarkResults.resolve("benchmark-results.csv"));
        log.info("Benchmark complete. Results written to: {}", benchmarkResults.toAbsolutePath());
    }

    private static BenchmarkResult runBenchmark(Crawler.Mode mode, FrontierQueue queue, int numThreads, int urls, int depth) throws InterruptedException {
        CrawlerContext context = new CrawlerContext(queue, new Fetcher(), new Parser(), new FileContentStorage(), new UrlMetadataStorage(), new BloomFilter(urls, 0.01), depth);
        Crawler crawler = new Crawler(context, numThreads, mode);

        Instant startTime = Instant.now();
        crawler.startCrawling();

        //wait until all URLs are processed
        while (!queue.isEmpty()) {
            Thread.sleep(100);
        }
        crawler.shutdown();

        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        long totalTimeMillis = duration.toMillis();
        double totalTimeSeconds = totalTimeMillis / 1000.0;
        int totalProcessed = context.getProcessedUrls();

        //import OperatingSystemMXBean from com.sun.management, not java.lang.management
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

        long memoryUsed = memoryBean.getHeapMemoryUsage().getUsed();
        int peakThreads = threadBean.getPeakThreadCount();
        double cpuLoad = osBean.getCpuLoad();

        //TODO: add error count logic in startCrawling
        return new BenchmarkResult(mode.name(), totalProcessed, 0, totalTimeMillis, totalProcessed / totalTimeSeconds, memoryUsed, peakThreads, cpuLoad);
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
            writer.println("Mode                |   URL Processed   |   ERROR_COUNT     |   TotalTimeMillis     |   Throughput(URLs/sec)    |   memoryUsedBytes     |       peakThreadCount     |   cpuLoad");
            for (BenchmarkResult result : results) {
                writer.printf("%s   |   %d  |   %d  |   %d  |   %.2f    |   %d      |       %d      |   %.2f\n",
                        result.mode, result.totalProcessed, result.errorCount, result.totalTimeMillis, result.throughput,
                        result.memoryUsedBytes, result.peakThreadCount, result.cpuLoad);
            }
        }
    }
}