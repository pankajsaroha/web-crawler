package com.github.pankajsaroha.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UrlMetadata {
    private String url;
    private String title;
    private String filePath;
    private int statusCode;
    private long fetchDuration;
    private int retry;
    private Status status;
}
