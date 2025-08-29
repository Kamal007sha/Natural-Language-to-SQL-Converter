package com.nlsql.model;

import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryRequest {
    
    @NotBlank(message = "Query cannot be empty")
    @JsonProperty("query")
    private String query;
    
    public QueryRequest() {}
    
    public QueryRequest(String query) {
        this.query = query;
    }
    
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
}