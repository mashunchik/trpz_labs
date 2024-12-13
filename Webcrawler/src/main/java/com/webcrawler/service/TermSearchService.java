package com.webcrawler.service;

import com.webcrawler.model.SearchTerm;
import com.webcrawler.repository.SearchTermRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class TermSearchService {
    private final SearchTermRepository searchTermRepository;
    private static final int CONTEXT_LENGTH = 100; // characters before and after the term

    @Transactional
    public SearchTerm addSearchTerm(String term) {
        return searchTermRepository.findByTerm(term)
                .orElseGet(() -> {
                    SearchTerm newTerm = new SearchTerm();
                    newTerm.setTerm(term);
                    return searchTermRepository.save(newTerm);
                });
    }

    @Transactional
    public void processPageForTerms(String url, Document document) {
        String text = document.text();
        searchTermRepository.findAll().forEach(searchTerm -> 
            processTermInPage(searchTerm, url, text));
    }

    private void processTermInPage(SearchTerm searchTerm, String url, String pageText) {
        Pattern pattern = Pattern.compile(searchTerm.getTerm(), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(pageText);
        
        List<String> contexts = new ArrayList<>();
        int count = 0;
        
        while (matcher.find()) {
            count++;
            contexts.add(extractContext(pageText, matcher.start(), matcher.end()));
            if (contexts.size() >= 5) break; // Limit to 5 context snippets per page
        }
        
        if (count > 0) {
            searchTerm.getFoundUrls().add(url);
            searchTerm.setOccurrenceCount(searchTerm.getOccurrenceCount() + count);
            searchTerm.setContextSnippets(String.join("\n---\n", contexts));
            searchTermRepository.save(searchTerm);
            log.info("Found {} occurrences of term '{}' in {}", count, searchTerm.getTerm(), url);
        }
    }

    private String extractContext(String text, int termStart, int termEnd) {
        int contextStart = Math.max(0, termStart - CONTEXT_LENGTH);
        int contextEnd = Math.min(text.length(), termEnd + CONTEXT_LENGTH);
        
        String context = text.substring(contextStart, contextEnd);
        if (contextStart > 0) context = "..." + context;
        if (contextEnd < text.length()) context = context + "...";
        
        return context;
    }

    public List<SearchTerm> getAllSearchTerms() {
        return searchTermRepository.findAll();
    }

    public Optional<SearchTerm> getSearchTerm(String term) {
        return searchTermRepository.findByTerm(term);
    }
}
