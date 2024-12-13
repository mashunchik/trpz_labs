package com.webcrawler.repository;

import com.webcrawler.model.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    Optional<Statistics> findByDomain(String domain);
    boolean existsByDomain(String domain);
}
