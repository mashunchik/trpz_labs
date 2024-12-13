package com.webcrawler.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "search_terms")
public class SearchTerm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String term;

    @ElementCollection
    @CollectionTable(name = "term_occurrences", 
        joinColumns = @JoinColumn(name = "search_term_id"))
    private Set<String> foundUrls = new HashSet<>();

    private int occurrenceCount;
    
    @Column(columnDefinition = "TEXT")
    private String contextSnippets;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
