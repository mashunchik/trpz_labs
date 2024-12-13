package com.webcrawler.service;

import com.webcrawler.http.ProxyHttpClient;
import com.webcrawler.model.*;
import com.webcrawler.repository.*;
import com.webcrawler.service.TermSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebCrawlerService {
    private final CrawlQueueRepository crawlQueueRepository;
    private final ScannedPageRepository scannedPageRepository;
    private final MetadataRepository metadataRepository;
    private final StatisticsRepository statisticsRepository;
    private final ProxyHttpClient httpClient;
    private final TermSearchService termSearchService;

    @Transactional
    public void addUrlToCrawl(String url, int priority) {
        if (!crawlQueueRepository.existsByUrl(url)) {
            CrawlQueue queueItem = new CrawlQueue();
            queueItem.setUrl(url);
            queueItem.setPriority(priority);
            queueItem.setStatus(CrawlQueue.CrawlStatus.PENDING);
            queueItem.setRetryCount(0);
            crawlQueueRepository.save(queueItem);
            log.info("Added URL to crawl queue: {}", url);
        }
    }

    @Transactional
    public void crawlNextPage() {
        crawlQueueRepository.findByStatusOrderByPriorityDesc(CrawlQueue.CrawlStatus.PENDING)
                .stream()
                .findFirst()
                .ifPresent(this::processCrawlRequest);
    }

    private void processCrawlRequest(CrawlQueue queueItem) {
        try {
            queueItem.setStatus(CrawlQueue.CrawlStatus.IN_PROGRESS);
            crawlQueueRepository.save(queueItem);

            Document doc = httpClient.fetchPage(queueItem.getUrl());

            processPage(queueItem.getUrl(), doc);
            queueItem.setStatus(CrawlQueue.CrawlStatus.COMPLETED);
            
        } catch (IOException e) {
            log.error("Error crawling URL: " + queueItem.getUrl(), e);
            handleCrawlError(queueItem);
        }
        
        crawlQueueRepository.save(queueItem);
    }

    private void processPage(String url, Document doc) {
        // Save metadata
        saveMetadata(url, doc);
        
        // Save scanned page
        saveScannedPage(url, doc);
        
        // Process search terms
        termSearchService.processPageForTerms(url, doc);
        
        // Extract and queue new links
        extractLinks(doc).forEach(link -> addUrlToCrawl(link, 1));
        
        // Update statistics
        updateStatistics(url);
    }

    private void saveMetadata(String url, Document doc) {
        Metadata metadata = new Metadata();
        metadata.setUrl(url);
        metadata.setTitle(doc.title());
        metadata.setDescription(doc.select("meta[name=description]").attr("content"));
        metadata.setKeywords(doc.select("meta[name=keywords]").attr("content"));
        metadata.setLanguage(doc.select("html").attr("lang"));
        metadata.setContentType("text/html");
        metadata.setPageSize((long) doc.html().length());
        metadataRepository.save(metadata);
    }

    private void saveScannedPage(String url, Document doc) {
        ScannedPage scannedPage = new ScannedPage();
        scannedPage.setUrl(url);
        scannedPage.setCleanedHtmlPath(saveCleanedHtml(doc));
        scannedPage.setContentHash(String.valueOf(doc.html().hashCode()));
        scannedPage.setStatus(ScannedPage.ProcessingStatus.PROCESSED);
        scannedPage.setLastScanTime(LocalDateTime.now());
        scannedPageRepository.save(scannedPage);
    }

    private String saveCleanedHtml(Document doc) {
        // Remove unwanted elements
        doc.select("script, style, iframe, .advertisement").remove();
        
        // Create a cleaned HTML file path (implement actual file saving logic)
        return "cleaned/" + doc.location().replaceAll("[^a-zA-Z0-9]", "_") + ".html";
    }

    private Set<String> extractLinks(Document doc) {
        return doc.select("a[href]").stream()
                .map(element -> element.attr("abs:href"))
                .filter(url -> url.startsWith("http"))
                .collect(Collectors.toCollection(HashSet::new));
    }

    private void updateStatistics(String url) {
        String domain = extractDomain(url);
        Statistics stats = statisticsRepository.findByDomain(domain)
                .orElse(new Statistics());
        
        stats.setDomain(domain);
        stats.setVisitCount(stats.getVisitCount() + 1);
        stats.setLastVisitTime(LocalDateTime.now());
        stats.setTotalPagesCrawled(stats.getTotalPagesCrawled() + 1);
        
        statisticsRepository.save(stats);
    }

    private void handleCrawlError(CrawlQueue queueItem) {
        queueItem.setRetryCount(queueItem.getRetryCount() + 1);
        if (queueItem.getRetryCount() >= 3) {
            queueItem.setStatus(CrawlQueue.CrawlStatus.FAILED);
        } else {
            queueItem.setStatus(CrawlQueue.CrawlStatus.PENDING);
        }
    }

    private String extractDomain(String url) {
        return url.replaceAll("https?://([^/]+).*", "$1");
    }

    // Additional methods for getting statistics and status
    public List<Statistics> getAllStatistics() {
        return statisticsRepository.findAll();
    }

    public List<ScannedPage> getScannedPages() {
        return scannedPageRepository.findAll();
    }
}
