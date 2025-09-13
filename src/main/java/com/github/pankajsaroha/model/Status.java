package com.github.pankajsaroha.model;

public enum Status {
    DISCOVERED, //found but not yet crawled
    IN_PROGRESS, //currently being fetched
    SUCCESS, //successfully fetched
    FAILED, //permanently failed
    RETRY //scheduled for retry
}
