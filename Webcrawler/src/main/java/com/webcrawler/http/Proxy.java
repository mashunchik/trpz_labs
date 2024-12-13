package com.webcrawler.http;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Proxy {
    private String host;
    private int port;
    private String username;
    private String password;
    private ProxyType type;
    private boolean available = true;
    private int failCount = 0;
    private long lastUsed = 0;

    public enum ProxyType {
        HTTP, SOCKS4, SOCKS5
    }

    public Proxy(String host, int port) {
        this.host = host;
        this.port = port;
        this.type = ProxyType.HTTP;
    }

    public void markFailed() {
        failCount++;
        if (failCount >= 3) {
            available = false;
        }
    }

    public void markSuccess() {
        failCount = 0;
        available = true;
    }

    public void updateLastUsed() {
        lastUsed = System.currentTimeMillis();
    }
}
