package com.webcrawler.http;

import org.jsoup.nodes.Document;
import java.io.IOException;

public interface HttpClient {
    Document fetchPage(String url) throws IOException;
    void setProxy(Proxy proxy);
    boolean isAvailable();
}
