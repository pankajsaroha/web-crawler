package com.github.pankajsaroha.model;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class Page {
    private final String url;
    private String title;
    private String content; //raw HTML content
    private List<String> links;
    private int statusCode;
    private long fetchDurationMs;

}
