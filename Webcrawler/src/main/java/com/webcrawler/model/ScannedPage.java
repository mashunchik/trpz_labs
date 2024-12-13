package com.webcrawler.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "scanned_pages")
public class ScannedPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String url;
    
    private String rawHtmlPath;
    private String cleanedHtmlPath;
    private String extractedContent;
    
    @Enumerated(EnumType.STRING)
    private ProcessingStatus status;
    
    private LocalDateTime lastScanTime;
    private String contentHash;
    
    @Column(columnDefinition = "TEXT")
    private String foundLinks;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum ProcessingStatus {
        PENDING,
        PROCESSED,
        FAILED
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lastScanTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
