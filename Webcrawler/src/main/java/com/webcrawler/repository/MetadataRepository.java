package com.webcrawler.repository;

import com.webcrawler.model.Metadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MetadataRepository extends JpaRepository<Metadata, Long> {
    Optional<Metadata> findByUrl(String url);
    boolean existsByUrl(String url);
    void deleteByUrl(String url);
}
