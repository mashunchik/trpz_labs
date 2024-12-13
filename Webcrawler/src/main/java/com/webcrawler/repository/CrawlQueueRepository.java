package com.webcrawler.repository;

import com.webcrawler.model.CrawlQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CrawlQueueRepository extends JpaRepository<CrawlQueue, Long> {
    Optional<CrawlQueue> findByUrl(String url);
    List<CrawlQueue> findByStatus(CrawlQueue.CrawlStatus status);
    List<CrawlQueue> findByStatusOrderByPriorityDesc(CrawlQueue.CrawlStatus status);
    boolean existsByUrl(String url);
}
