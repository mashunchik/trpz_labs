package com.webcrawler.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProxyHttpClient implements HttpClient {
    private final ProxyManager proxyManager;
    private final RealHttpClient realHttpClient;
    private Proxy currentProxy;
    private static final AtomicInteger requestCount = new AtomicInteger(0);
    private static final int REQUESTS_PER_PROXY = 50;

    public ProxyHttpClient(List<Proxy> initialProxies) {
        this.proxyManager = new ProxyManager(initialProxies);
        this.realHttpClient = new RealHttpClient();
    }

    @Override
    public Document fetchPage(String url) throws IOException {
        if (shouldRotateProxy()) {
            rotateProxy();
        }

        try {
            Document document = realHttpClient.fetchPage(url);
            handleSuccess();
            return document;
        } catch (IOException e) {
            handleFailure();
            throw e;
        }
    }

    private boolean shouldRotateProxy() {
        return currentProxy == null ||
               !currentProxy.isAvailable() ||
               requestCount.get() >= REQUESTS_PER_PROXY;
    }

    private void rotateProxy() {
        currentProxy = proxyManager.getNextProxy();
        realHttpClient.setProxy(currentProxy);
        requestCount.set(0);
        log.info("Rotated to new proxy: {}:{}", currentProxy.getHost(), currentProxy.getPort());
    }

    private void handleSuccess() {
        requestCount.incrementAndGet();
        if (currentProxy != null) {
            currentProxy.markSuccess();
            currentProxy.updateLastUsed();
        }
    }

    private void handleFailure() {
        if (currentProxy != null) {
            currentProxy.markFailed();
            if (!currentProxy.isAvailable()) {
                rotateProxy();
            }
        }
    }

    @Override
    public void setProxy(Proxy proxy) {
        this.currentProxy = proxy;
        realHttpClient.setProxy(proxy);
    }

    @Override
    public boolean isAvailable() {
        return proxyManager.hasAvailableProxies();
    }
}
