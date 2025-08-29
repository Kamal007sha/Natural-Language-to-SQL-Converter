package com.nlsql.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class QueryResponse {
    
    @JsonProperty("sql")
    private String sql;
    
    @JsonProperty("results")
    private List<Map<String, Object>> results;
    
    @JsonProperty("error")
    private String error;
    
    @JsonProperty("execution_time_ms")
    private Long executionTimeMs;
    
    public QueryResponse() {}
    
    public QueryResponse(String sql, List<Map<String, Object>> results) {
        this.sql = sql;
        this.results = results;
    }
    
    public QueryResponse(String error) {
        this.error = error;
    }
    
    public String getSql() {
        return sql;
    }
    
    public void setSql(String sql) {
        this.sql = sql;
    }
    
    public List<Map<String, Object>> getResults() {
        return results;
    }
    
    public void setResults(List<Map<String, Object>> results) {
        this.results = results;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
}