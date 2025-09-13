package com.github.pankajsaroha.crawler;

import com.github.pankajsaroha.model.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Parser {

    public void parse(Page page) {
        Document document = Jsoup.parse(page.getContent());
        Elements links = document.select("a[href]");

        page.setTitle(document.title());

        for (var link : links) {
            String absUrl = link.absUrl("href");
            page.getLinks().add(absUrl);
        }
    }
}
