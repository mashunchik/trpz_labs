package com.webcrawler.controller.config;

import com.webcrawler.http.Proxy;
import com.webcrawler.http.ProxyHttpClient;
import com.webcrawler.http.ProxyManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class HttpClientConfig {

    @Bean
    public ProxyManager proxyManager() {
        List<Proxy> initialProxies = new ArrayList<>();
        // Add some default proxies
        initialProxies.add(new Proxy("proxy1.example.com", 8080));
        initialProxies.add(new Proxy("proxy2.example.com", 8080));
        initialProxies.add(new Proxy("proxy3.example.com", 8080));
        return new ProxyManager(initialProxies);
    }

    @Bean
    public ProxyHttpClient proxyHttpClient(ProxyManager proxyManager) {
        return new ProxyHttpClient(proxyManager.getAllProxies());
    }
}
