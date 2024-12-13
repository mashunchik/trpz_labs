package com.webcrawler.repository;

import com.webcrawler.model.ScannedPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScannedPageRepository extends JpaRepository<ScannedPage, Long> {
    Optional<ScannedPage> findByUrl(String url);
    List<ScannedPage> findByStatus(ScannedPage.ProcessingStatus status);
    List<ScannedPage> findByLastScanTimeBefore(LocalDateTime time);
    boolean existsByUrl(String url);
    void deleteByUrl(String url);
}
