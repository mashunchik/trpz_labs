package com.webcrawler.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class ProxyManager {
    private final List<Proxy> proxies;
    private final AtomicInteger currentIndex;
    private static final long COOLDOWN_PERIOD = 60000; // 1 minute cooldown

    public ProxyManager(List<Proxy> initialProxies) {
        this.proxies = new ArrayList<>(initialProxies);
        this.currentIndex = new AtomicInteger(0);
    }

    public synchronized Proxy getNextProxy() {
        int startIndex = currentIndex.get();
        int index = startIndex;
        
        do {
            Proxy proxy = proxies.get(index);
            if (isProxyAvailable(proxy)) {
                currentIndex.set((index + 1) % proxies.size());
                return proxy;
            }
            index = (index + 1) % proxies.size();
        } while (index != startIndex);

        // If no proxy is available, reset all proxies and try again
        resetFailedProxies();
        return proxies.get(currentIndex.get());
    }

    private boolean isProxyAvailable(Proxy proxy) {
        if (!proxy.isAvailable()) {
            return false;
        }

        long timeSinceLastUse = System.currentTimeMillis() - proxy.getLastUsed();
        return timeSinceLastUse >= COOLDOWN_PERIOD;
    }

    public void addProxy(Proxy proxy) {
        proxies.add(proxy);
    }

    public void removeProxy(Proxy proxy) {
        proxies.remove(proxy);
    }

    public boolean hasAvailableProxies() {
        return proxies.stream().anyMatch(Proxy::isAvailable);
    }

    private void resetFailedProxies() {
        proxies.forEach(proxy -> {
            proxy.setAvailable(true);
            proxy.setFailCount(0);
        });
        log.info("Reset all failed proxies");
    }

    public List<Proxy> getAllProxies() {
        return Collections.unmodifiableList(proxies);
    }
}
