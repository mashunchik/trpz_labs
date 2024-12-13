package com.webcrawler.controller;

import com.webcrawler.model.SearchTerm;
import com.webcrawler.service.TermSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SearchTermController {
    private final TermSearchService termSearchService;

    @PostMapping
    public ResponseEntity<SearchTerm> addSearchTerm(@RequestParam String term) {
        return ResponseEntity.ok(termSearchService.addSearchTerm(term));
    }

    @GetMapping
    public ResponseEntity<List<SearchTerm>> getAllSearchTerms() {
        return ResponseEntity.ok(termSearchService.getAllSearchTerms());
    }

    @GetMapping("/{term}")
    public ResponseEntity<SearchTerm> getSearchTerm(@PathVariable String term) {
        return termSearchService.getSearchTerm(term)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
