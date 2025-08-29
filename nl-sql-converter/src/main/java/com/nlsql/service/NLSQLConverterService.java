package com.nlsql.service;

import com.nlsql.model.ParsedQuery;
import com.nlsql.model.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NLSQLConverterService {
    
    private static final Logger logger = LoggerFactory.getLogger(NLSQLConverterService.class);
    
    @Autowired
    private NLPProcessorService nlpProcessorService;
    
    @Autowired
    private QueryExecutionService queryExecutionService;
    
    @Autowired
    private DatabaseSchemaService databaseSchemaService;
    
    public QueryResponse processQuery(String naturalLanguageQuery) {
        try {
            logger.debug("Processing natural language query: {}", naturalLanguageQuery);
            
            // Step 1: Parse natural language to extract intent and entities
            ParsedQuery parsedQuery = nlpProcessorService.parseQuery(naturalLanguageQuery);
            logger.debug("Parsed query: {}", parsedQuery.getQueryType());
            
            // Step 2: Map entities to database schema
            parsedQuery = databaseSchemaService.mapToSchema(parsedQuery);
            
            // Step 3: Generate SQL from parsed query
            String sql = generateSQL(parsedQuery);
            logger.debug("Generated SQL: {}", sql);
            
            // Step 4: Execute SQL query
            List<Map<String, Object>> results = queryExecutionService.executeQuery(sql);
            logger.debug("Query executed successfully, returned {} rows", results.size());
            
            return new QueryResponse(sql, results);
            
        } catch (Exception e) {
            logger.error("Error processing query: {}", e.getMessage(), e);
            return new QueryResponse("Error processing query: " + e.getMessage());
        }
    }
    
    private String generateSQL(ParsedQuery parsedQuery) {
        StringBuilder sql = new StringBuilder();
        
        switch (parsedQuery.getQueryType()) {
            case SELECT:
                sql.append("SELECT ");
                if (parsedQuery.getColumns() != null && !parsedQuery.getColumns().isEmpty()) {
                    sql.append(String.join(", ", parsedQuery.getColumns()));
                } else {
                    sql.append("*");
                }
                sql.append(" FROM ").append(parsedQuery.getTableName());
                break;
                
            case COUNT:
                sql.append("SELECT COUNT(*) as count FROM ").append(parsedQuery.getTableName());
                break;
                
            default:
                throw new UnsupportedOperationException("Query type not supported: " + parsedQuery.getQueryType());
        }
        
        // Add WHERE conditions
        if (parsedQuery.getWhereConditions() != null && !parsedQuery.getWhereConditions().isEmpty()) {
            sql.append(" WHERE ");
            boolean first = true;
            for (Map.Entry<String, Object> condition : parsedQuery.getWhereConditions().entrySet()) {
                if (!first) {
                    sql.append(" AND ");
                }
                sql.append(condition.getKey()).append(" = '").append(condition.getValue()).append("'");
                first = false;
            }
        }
        
        // Add GROUP BY
        if (parsedQuery.getGroupByColumns() != null && !parsedQuery.getGroupByColumns().isEmpty()) {
            sql.append(" GROUP BY ").append(String.join(", ", parsedQuery.getGroupByColumns()));
        }
        
        // Add ORDER BY
        if (parsedQuery.getOrderBy() != null) {
            sql.append(" ORDER BY ").append(parsedQuery.getOrderBy());
            if (parsedQuery.getOrderDirection() != null) {
                sql.append(" ").append(parsedQuery.getOrderDirection());
            }
        }
        
        // Add LIMIT
        if (parsedQuery.getLimit() != null) {
            sql.append(" LIMIT ").append(parsedQuery.getLimit());
        }
        
        return sql.toString();
    }
}