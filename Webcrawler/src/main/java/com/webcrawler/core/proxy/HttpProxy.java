package com.webcrawler.core.proxy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Proxy Pattern implementation
public class HttpProxy {
    private static final int TIMEOUT_MS = 10000;
    private final Map<String, Document> cache;
    private final Map<String, Long> lastAccessTime;
    private static final long CACHE_DURATION_MS = 3600000; // 1 hour

    public HttpProxy() {
        this.cache = new ConcurrentHashMap<>();
        this.lastAccessTime = new ConcurrentHashMap<>();
    }

    public Document fetch(String url) throws IOException {
        // Check cache first
        if (cache.containsKey(url)) {
            long lastAccess = lastAccessTime.get(url);
            if (System.currentTimeMillis() - lastAccess < CACHE_DURATION_MS) {
                return cache.get(url);
            } else {
                cache.remove(url);
                lastAccessTime.remove(url);
            }
        }

        // Fetch from network
        Document doc = Jsoup.connect(url)
                .timeout(TIMEOUT_MS)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .get();

        // Cache the result
        cache.put(url, doc);
        lastAccessTime.put(url, System.currentTimeMillis());

        return doc;
    }

    public void clearCache() {
        cache.clear();
        lastAccessTime.clear();
    }
}
