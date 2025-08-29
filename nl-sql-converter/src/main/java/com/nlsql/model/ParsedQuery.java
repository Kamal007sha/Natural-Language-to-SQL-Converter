package com.nlsql.model;

import java.util.List;
import java.util.Map;

public class ParsedQuery {
    
    private QueryType queryType;
    private String tableName;
    private List<String> columns;
    private Map<String, Object> whereConditions;
    private List<String> groupByColumns;
    private String orderBy;
    private String orderDirection;
    private Integer limit;
    
    public enum QueryType {
        SELECT, COUNT, INSERT, UPDATE, DELETE
    }
    
    public ParsedQuery() {}
    
    public QueryType getQueryType() {
        return queryType;
    }
    
    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public List<String> getColumns() {
        return columns;
    }
    
    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
    
    public Map<String, Object> getWhereConditions() {
        return whereConditions;
    }
    
    public void setWhereConditions(Map<String, Object> whereConditions) {
        this.whereConditions = whereConditions;
    }
    
    public List<String> getGroupByColumns() {
        return groupByColumns;
    }
    
    public void setGroupByColumns(List<String> groupByColumns) {
        this.groupByColumns = groupByColumns;
    }
    
    public String getOrderBy() {
        return orderBy;
    }
    
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
    
    public String getOrderDirection() {
        return orderDirection;
    }
    
    public void setOrderDirection(String orderDirection) {
        this.orderDirection = orderDirection;
    }
    
    public Integer getLimit() {
        return limit;
    }
    
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}