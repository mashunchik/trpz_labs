package com.webcrawler.controller;

import com.webcrawler.model.ScannedPage;
import com.webcrawler.model.Statistics;
import com.webcrawler.service.WebCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crawler")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WebCrawlerController {
    private final WebCrawlerService webCrawlerService;

    @PostMapping("/crawl")
    public ResponseEntity<String> startCrawling(@RequestParam String url, 
                                              @RequestParam(defaultValue = "1") int priority) {
        webCrawlerService.addUrlToCrawl(url, priority);
        webCrawlerService.crawlNextPage();
        return ResponseEntity.ok("Crawling started for URL: " + url);
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<Statistics>> getStatistics() {
        return ResponseEntity.ok(webCrawlerService.getAllStatistics());
    }

    @GetMapping("/pages")
    public ResponseEntity<List<ScannedPage>> getScannedPages() {
        return ResponseEntity.ok(webCrawlerService.getScannedPages());
    }
}
