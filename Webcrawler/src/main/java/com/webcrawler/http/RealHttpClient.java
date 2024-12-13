package com.webcrawler.http;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

@Slf4j
public class RealHttpClient implements HttpClient {
    private Proxy proxy;
    private static final int TIMEOUT = 10000;

    @Override
    public Document fetchPage(String url) throws IOException {
        if (proxy != null) {
            return Jsoup.connect(url)
                    .proxy(proxy.getHost(), proxy.getPort())
                    .userAgent("Mozilla/5.0")
                    .timeout(TIMEOUT)
                    .get();
        } else {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(TIMEOUT)
                    .get();
        }
    }

    @Override
    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
