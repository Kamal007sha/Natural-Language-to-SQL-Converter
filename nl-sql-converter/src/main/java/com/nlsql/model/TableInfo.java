package com.nlsql.model;

import java.util.List;

public class TableInfo {
    
    private String tableName;
    private List<ColumnInfo> columns;
    private List<String> aliases;
    
    public TableInfo() {}
    
    public TableInfo(String tableName, List<ColumnInfo> columns) {
        this.tableName = tableName;
        this.columns = columns;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public List<ColumnInfo> getColumns() {
        return columns;
    }
    
    public void setColumns(List<ColumnInfo> columns) {
        this.columns = columns;
    }
    
    public List<String> getAliases() {
        return aliases;
    }
    
    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }
    
    public static class ColumnInfo {
        private String columnName;
        private String dataType;
        private boolean nullable;
        private List<String> aliases;
        
        public ColumnInfo() {}
        
        public ColumnInfo(String columnName, String dataType, boolean nullable) {
            this.columnName = columnName;
            this.dataType = dataType;
            this.nullable = nullable;
        }
        
        public String getColumnName() {
            return columnName;
        }
        
        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }
        
        public String getDataType() {
            return dataType;
        }
        
        public void setDataType(String dataType) {
            this.dataType = dataType;
        }
        
        public boolean isNullable() {
            return nullable;
        }
        
        public void setNullable(boolean nullable) {
            this.nullable = nullable;
        }
        
        public List<String> getAliases() {
            return aliases;
        }
        
        public void setAliases(List<String> aliases) {
            this.aliases = aliases;
        }
    }
}