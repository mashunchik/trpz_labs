package com.webcrawler.repository;

import com.webcrawler.model.SearchTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SearchTermRepository extends JpaRepository<SearchTerm, Long> {
    Optional<SearchTerm> findByTerm(String term);
    boolean existsByTerm(String term);
}
