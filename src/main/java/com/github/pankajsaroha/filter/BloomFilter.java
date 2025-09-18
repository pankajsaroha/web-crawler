package com.github.pankajsaroha.filter;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

@Slf4j
public class BloomFilter {
    private final BitSet bitSet;
    private final int bitSize;
    private final int numHashFunctions;
    private final long FNV_PRIME = 0x100000001b3L;
    private final long OFFSET_BASIS = 0xcbf29ce484222325L;

    public BloomFilter(int n, double falsePositiveRate) {
        this.bitSize = optimalBitSize(n, falsePositiveRate);
        this.bitSet = new BitSet(bitSize);
        this.numHashFunctions = optimalNumHashes(n, bitSize);
    }

    private int optimalBitSize(int n, double p) {
        return (int) Math.ceil(-(n * Math.log(p)) / (Math.log(2) * Math.log(2)));
    }

    private int optimalNumHashes(int n, int m) {
        return (int) (Math.round((double) m / n) * Math.log(2));
    }

    public void add(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        long h1 = hash(bytes, 0);
        long h2 = hash(bytes, 42);

        for (int i = 0; i < numHashFunctions; i++) {
            long bit = (h1 + i * h2) % bitSize;
            if (bit < 0) {
                bit += bitSize;
            }
            bitSet.set((int) bit);
        }
    }

    public boolean search(String value) {
        if (value.isEmpty()) return true;
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        long h1 = hash(bytes, 0);
        long h2 = hash(bytes, 42);

        for (int i = 0; i < numHashFunctions; i++) {
            long bit = (h1 + i * h2) % bitSize;
            if (bit < 0) {
                bit += bitSize;
            }
            if (bitSet.get((int) bit)) {
                log.info("Duplicate url found: " + value);
                return true;
            }
        }
        return false;
    }

    /**
     * Simple FNV1a hashing algorithm to calculate the hash of the url
     * @param bytes (url)
     * @return hash of the value
     */
    private long hash(byte[] bytes, long seed) {
        long hash = OFFSET_BASIS ^ seed;

        for (byte b : bytes) {
            hash ^= (b & 0xff);
            hash *= FNV_PRIME;
        }

        return hash;
    }
}
