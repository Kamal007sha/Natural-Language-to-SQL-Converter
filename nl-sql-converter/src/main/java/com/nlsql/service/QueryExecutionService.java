package com.nlsql.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QueryExecutionService {
    
    private static final Logger logger = LoggerFactory.getLogger(QueryExecutionService.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Value("${nlsql.query.max-results:1000}")
    private int maxResults;
    
    public List<Map<String, Object>> executeQuery(String sql) {
        logger.debug("Executing SQL: {}", sql);
        
        try {
            // Validate SQL to prevent dangerous operations
            validateSQL(sql);
            
            // Add limit if not present to prevent excessive results
            String limitedSql = addLimitIfNeeded(sql);
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(limitedSql);
            logger.debug("Query executed successfully, returned {} rows", results.size());
            
            return results;
            
        } catch (Exception e) {
            logger.error("Error executing SQL: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to execute query: " + e.getMessage());
        }
    }
    
    private void validateSQL(String sql) {
        String normalizedSql = sql.toLowerCase().trim();
        
        // Only allow SELECT and COUNT queries for safety
        if (!normalizedSql.startsWith("select")) {
            throw new SecurityException("Only SELECT queries are allowed");
        }
        
        // Prevent dangerous SQL operations
        String[] dangerousKeywords = {
            "drop", "delete", "update", "insert", "alter", "create", 
            "truncate", "exec", "execute", "sp_", "xp_", "union", 
            "information_schema", "pg_", "mysql", "sys", "master"
        };
        
        for (String keyword : dangerousKeywords) {
            if (normalizedSql.contains(keyword)) {
                throw new SecurityException("SQL contains potentially dangerous keyword: " + keyword);
            }
        }
        
        // Check for SQL injection patterns
        if (normalizedSql.matches(".*[';].*|.*--.*|.*\\/\\*.*\\*\\/.*")) {
            throw new SecurityException("SQL contains potentially malicious patterns");
        }
    }
    
    private String addLimitIfNeeded(String sql) {
        String normalizedSql = sql.toLowerCase();
        
        // If LIMIT is already present, don't add another one
        if (normalizedSql.contains("limit")) {
            return sql;
        }
        
        // Add LIMIT to prevent excessive results
        return sql + " LIMIT " + maxResults;
    }
    
    public boolean testConnection() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return true;
        } catch (Exception e) {
            logger.error("Database connection test failed: {}", e.getMessage());
            return false;
        }
    }
}