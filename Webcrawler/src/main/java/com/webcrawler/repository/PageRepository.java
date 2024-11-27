package com.webcrawler.repository;

import com.webcrawler.model.Page;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PageRepository implements Repository<Page, Long> {
    private static final String DB_URL = "jdbc:sqlite:crawler.db";
    
    public PageRepository() {
        initializeDatabase();
    }
    
    private void initializeDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS pages (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                url TEXT UNIQUE NOT NULL,
                title TEXT,
                content TEXT,
                crawled_at TIMESTAMP,
                links TEXT,
                metadata TEXT
            )
        """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    @Override
    public Page save(Page page) {
        String sql = """
            INSERT OR REPLACE INTO pages (url, title, content, crawled_at, links, metadata)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, page.getUrl());
            pstmt.setString(2, page.getTitle());
            pstmt.setString(3, page.getContent());
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(5, String.join(",", page.getLinks()));
            pstmt.setString(6, page.getMetadata());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    page.setId(generatedKeys.getLong(1));
                }
            }
            
            return page;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save page", e);
        }
    }
    
    @Override
    public Optional<Page> findById(Long id) {
        String sql = "SELECT * FROM pages WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToPage(rs));
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find page by id", e);
        }
    }
    
    @Override
    public List<Page> findAll() {
        String sql = "SELECT * FROM pages";
        List<Page> pages = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                pages.add(mapResultSetToPage(rs));
            }
            
            return pages;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch all pages", e);
        }
    }
    
    @Override
    public void delete(Page page) {
        deleteById(page.getId());
    }
    
    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM pages WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete page", e);
        }
    }
    
    @Override
    public boolean exists(Long id) {
        String sql = "SELECT COUNT(*) FROM pages WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check if page exists", e);
        }
    }
    
    private Page mapResultSetToPage(ResultSet rs) throws SQLException {
        Page page = new Page();
        page.setId(rs.getLong("id"));
        page.setUrl(rs.getString("url"));
        page.setTitle(rs.getString("title"));
        page.setContent(rs.getString("content"));
        page.setCrawledAt(rs.getTimestamp("crawled_at").toLocalDateTime());
        
        String links = rs.getString("links");
        if (links != null && !links.isEmpty()) {
            page.getLinks().addAll(List.of(links.split(",")));
        }
        
        page.setMetadata(rs.getString("metadata"));
        return page;
    }
}
