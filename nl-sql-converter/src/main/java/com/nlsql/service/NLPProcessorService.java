package com.nlsql.service;

import com.nlsql.model.ParsedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NLPProcessorService {
    
    private static final Logger logger = LoggerFactory.getLogger(NLPProcessorService.class);
    
    private static final Map<String, String> TABLE_ALIASES = Map.ofEntries(
        Map.entry("employees", "employees"),
        Map.entry("staff", "employees"),
        Map.entry("workers", "employees"),
        Map.entry("people", "employees"),
        Map.entry("users", "users"),
        Map.entry("customers", "customers"),
        Map.entry("clients", "customers"),
        Map.entry("orders", "orders"),
        Map.entry("purchases", "orders"),
        Map.entry("products", "products"),
        Map.entry("items", "products")
    );
    
    private static final Map<String, String> COLUMN_ALIASES = Map.ofEntries(
        Map.entry("name", "name"),
        Map.entry("names", "name"),
        Map.entry("full name", "name"),
        Map.entry("email", "email"),
        Map.entry("emails", "email"),
        Map.entry("city", "city"),
        Map.entry("location", "city"),
        Map.entry("department", "department"),
        Map.entry("dept", "department"),
        Map.entry("salary", "salary"),
        Map.entry("wage", "salary"),
        Map.entry("age", "age"),
        Map.entry("id", "id"),
        Map.entry("identifier", "id")
    );
    
    public ParsedQuery parseQuery(String query) {
        logger.debug("Parsing query: {}", query);
        
        String normalizedQuery = query.toLowerCase().trim();
        ParsedQuery parsedQuery = new ParsedQuery();
        
        // Determine query type
        if (containsAny(normalizedQuery, Arrays.asList("count", "how many", "number of"))) {
            parsedQuery.setQueryType(ParsedQuery.QueryType.COUNT);
        } else if (containsAny(normalizedQuery, Arrays.asList("show", "list", "get", "find", "select", "display"))) {
            parsedQuery.setQueryType(ParsedQuery.QueryType.SELECT);
        } else {
            parsedQuery.setQueryType(ParsedQuery.QueryType.SELECT); // Default
        }
        
        // Extract table name
        String tableName = extractTableName(normalizedQuery);
        parsedQuery.setTableName(tableName);
        
        // Extract columns
        List<String> columns = extractColumns(normalizedQuery);
        parsedQuery.setColumns(columns);
        
        // Extract WHERE conditions
        Map<String, Object> whereConditions = extractWhereConditions(normalizedQuery);
        parsedQuery.setWhereConditions(whereConditions);
        
        // Extract GROUP BY
        List<String> groupByColumns = extractGroupBy(normalizedQuery);
        parsedQuery.setGroupByColumns(groupByColumns);
        
        // Extract ORDER BY
        String orderBy = extractOrderBy(normalizedQuery);
        parsedQuery.setOrderBy(orderBy);
        
        // Extract LIMIT
        Integer limit = extractLimit(normalizedQuery);
        parsedQuery.setLimit(limit);
        
        logger.debug("Parsed query type: {}, table: {}", parsedQuery.getQueryType(), parsedQuery.getTableName());
        
        return parsedQuery;
    }
    
    private boolean containsAny(String text, List<String> keywords) {
        return keywords.stream().anyMatch(text::contains);
    }
    
    private String extractTableName(String query) {
        // Look for table aliases
        for (Map.Entry<String, String> entry : TABLE_ALIASES.entrySet()) {
            if (query.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // Default table
        return "employees";
    }
    
    private List<String> extractColumns(String query) {
        List<String> columns = new ArrayList<>();
        
        // Check for specific column mentions
        for (Map.Entry<String, String> entry : COLUMN_ALIASES.entrySet()) {
            if (query.contains(entry.getKey())) {
                columns.add(entry.getValue());
            }
        }
        
        // If no specific columns found, return all columns for SELECT, null for COUNT
        if (columns.isEmpty()) {
            return null; // Will be handled as SELECT * or COUNT(*)
        }
        
        return columns;
    }
    
    private Map<String, Object> extractWhereConditions(String query) {
        Map<String, Object> conditions = new HashMap<>();
        
        // Extract city/location conditions
        Pattern cityPattern = Pattern.compile("(?:in|from|at)\\s+([a-zA-Z\\s]+?)(?:\\s|$|,|\\.)", Pattern.CASE_INSENSITIVE);
        Matcher cityMatcher = cityPattern.matcher(query);
        if (cityMatcher.find()) {
            String city = cityMatcher.group(1).trim();
            if (!city.isEmpty() && !isStopWord(city)) {
                conditions.put("city", city);
            }
        }
        
        // Extract department conditions
        Pattern deptPattern = Pattern.compile("(?:department|dept)\\s+([a-zA-Z\\s]+?)(?:\\s|$|,|\\.)", Pattern.CASE_INSENSITIVE);
        Matcher deptMatcher = deptPattern.matcher(query);
        if (deptMatcher.find()) {
            String dept = deptMatcher.group(1).trim();
            if (!dept.isEmpty() && !isStopWord(dept)) {
                conditions.put("department", dept);
            }
        }
        
        // Extract age conditions
        Pattern agePattern = Pattern.compile("(?:age|aged)\\s+(?:over|above|greater than|>)\\s+(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher ageMatcher = agePattern.matcher(query);
        if (ageMatcher.find()) {
            conditions.put("age >", Integer.parseInt(ageMatcher.group(1)));
        }
        
        return conditions;
    }
    
    private List<String> extractGroupBy(String query) {
        List<String> groupBy = new ArrayList<>();
        
        if (query.contains("by department") || query.contains("by dept")) {
            groupBy.add("department");
        }
        if (query.contains("by city") || query.contains("by location")) {
            groupBy.add("city");
        }
        
        return groupBy.isEmpty() ? null : groupBy;
    }
    
    private String extractOrderBy(String query) {
        if (query.contains("order by name") || query.contains("sort by name")) {
            return "name";
        }
        if (query.contains("order by salary") || query.contains("sort by salary")) {
            return "salary";
        }
        if (query.contains("order by age") || query.contains("sort by age")) {
            return "age";
        }
        
        return null;
    }
    
    private Integer extractLimit(String query) {
        Pattern limitPattern = Pattern.compile("(?:limit|top|first)\\s+(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = limitPattern.matcher(query);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        
        return null;
    }
    
    private boolean isStopWord(String word) {
        Set<String> stopWords = Set.of("the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", "by", "all");
        return stopWords.contains(word.toLowerCase());
    }
}