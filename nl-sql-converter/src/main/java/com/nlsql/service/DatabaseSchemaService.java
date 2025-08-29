package com.nlsql.service;

import com.nlsql.model.ParsedQuery;
import com.nlsql.model.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DatabaseSchemaService {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSchemaService.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private Map<String, TableInfo> schemaCache = new HashMap<>();
    
    public ParsedQuery mapToSchema(ParsedQuery parsedQuery) {
        logger.debug("Mapping parsed query to database schema");
        
        // Ensure table exists and get correct table name
        String actualTableName = mapTableName(parsedQuery.getTableName());
        parsedQuery.setTableName(actualTableName);
        
        // Get table schema information
        TableInfo tableInfo = getTableInfo(actualTableName);
        
        // Map column names to actual column names
        if (parsedQuery.getColumns() != null) {
            List<String> mappedColumns = new ArrayList<>();
            for (String column : parsedQuery.getColumns()) {
                String actualColumn = mapColumnName(column, tableInfo);
                if (actualColumn != null) {
                    mappedColumns.add(actualColumn);
                }
            }
            parsedQuery.setColumns(mappedColumns.isEmpty() ? null : mappedColumns);
        }
        
        // Map WHERE condition column names
        if (parsedQuery.getWhereConditions() != null) {
            Map<String, Object> mappedConditions = new HashMap<>();
            for (Map.Entry<String, Object> entry : parsedQuery.getWhereConditions().entrySet()) {
                String actualColumn = mapColumnName(entry.getKey(), tableInfo);
                if (actualColumn != null) {
                    mappedConditions.put(actualColumn, entry.getValue());
                }
            }
            parsedQuery.setWhereConditions(mappedConditions);
        }
        
        // Map GROUP BY columns
        if (parsedQuery.getGroupByColumns() != null) {
            List<String> mappedGroupBy = new ArrayList<>();
            for (String column : parsedQuery.getGroupByColumns()) {
                String actualColumn = mapColumnName(column, tableInfo);
                if (actualColumn != null) {
                    mappedGroupBy.add(actualColumn);
                }
            }
            parsedQuery.setGroupByColumns(mappedGroupBy.isEmpty() ? null : mappedGroupBy);
        }
        
        // Map ORDER BY column
        if (parsedQuery.getOrderBy() != null) {
            String actualColumn = mapColumnName(parsedQuery.getOrderBy(), tableInfo);
            parsedQuery.setOrderBy(actualColumn);
        }
        
        return parsedQuery;
    }
    
    private String mapTableName(String tableName) {
        // Check if table exists as-is
        if (tableExists(tableName)) {
            return tableName;
        }
        
        // Try common variations
        String[] variations = {
            tableName.toLowerCase(),
            tableName.toUpperCase(),
            tableName + "s", // plural
            tableName.substring(0, tableName.length() - 1) // singular
        };
        
        for (String variation : variations) {
            if (tableExists(variation)) {
                return variation;
            }
        }
        
        // Default fallback - create sample table if it doesn't exist
        createSampleTables();
        return "employees";
    }
    
    private String mapColumnName(String columnName, TableInfo tableInfo) {
        // Direct match
        for (TableInfo.ColumnInfo column : tableInfo.getColumns()) {
            if (column.getColumnName().equalsIgnoreCase(columnName)) {
                return column.getColumnName();
            }
        }
        
        // Try aliases
        for (TableInfo.ColumnInfo column : tableInfo.getColumns()) {
            if (column.getAliases() != null) {
                for (String alias : column.getAliases()) {
                    if (alias.equalsIgnoreCase(columnName)) {
                        return column.getColumnName();
                    }
                }
            }
        }
        
        // Partial match
        for (TableInfo.ColumnInfo column : tableInfo.getColumns()) {
            if (column.getColumnName().toLowerCase().contains(columnName.toLowerCase()) ||
                columnName.toLowerCase().contains(column.getColumnName().toLowerCase())) {
                return column.getColumnName();
            }
        }
        
        return null;
    }
    
    public TableInfo getTableInfo(String tableName) {
        if (schemaCache.containsKey(tableName)) {
            return schemaCache.get(tableName);
        }
        
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(tableName);
        
        try {
            String sql = "SELECT column_name, data_type, is_nullable FROM information_schema.columns WHERE table_name = ? ORDER BY ordinal_position";
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(sql, tableName);
            
            List<TableInfo.ColumnInfo> columnInfos = new ArrayList<>();
            for (Map<String, Object> column : columns) {
                TableInfo.ColumnInfo columnInfo = new TableInfo.ColumnInfo();
                columnInfo.setColumnName((String) column.get("column_name"));
                columnInfo.setDataType((String) column.get("data_type"));
                columnInfo.setNullable("YES".equals(column.get("is_nullable")));
                
                // Add common aliases for columns
                columnInfo.setAliases(getColumnAliases(columnInfo.getColumnName()));
                
                columnInfos.add(columnInfo);
            }
            
            tableInfo.setColumns(columnInfos);
            schemaCache.put(tableName, tableInfo);
            
        } catch (Exception e) {
            logger.warn("Could not retrieve schema for table {}: {}", tableName, e.getMessage());
            // Return default schema
            tableInfo.setColumns(getDefaultColumns());
        }
        
        return tableInfo;
    }
    
    private List<String> getColumnAliases(String columnName) {
        Map<String, List<String>> aliases = Map.of(
            "id", Arrays.asList("identifier", "emp_id", "employee_id"),
            "name", Arrays.asList("full_name", "employee_name", "first_name"),
            "email", Arrays.asList("email_address", "mail"),
            "city", Arrays.asList("location", "address", "place"),
            "department", Arrays.asList("dept", "division", "team"),
            "salary", Arrays.asList("wage", "pay", "compensation"),
            "age", Arrays.asList("years", "old")
        );
        
        return aliases.getOrDefault(columnName.toLowerCase(), new ArrayList<>());
    }
    
    private List<TableInfo.ColumnInfo> getDefaultColumns() {
        return Arrays.asList(
            new TableInfo.ColumnInfo("id", "integer", false),
            new TableInfo.ColumnInfo("name", "varchar", false),
            new TableInfo.ColumnInfo("email", "varchar", true),
            new TableInfo.ColumnInfo("city", "varchar", true),
            new TableInfo.ColumnInfo("department", "varchar", true),
            new TableInfo.ColumnInfo("salary", "numeric", true),
            new TableInfo.ColumnInfo("age", "integer", true)
        );
    }
    
    private boolean tableExists(String tableName) {
        try {
            String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
            return count != null && count > 0;
        } catch (Exception e) {
            logger.debug("Error checking table existence: {}", e.getMessage());
            return false;
        }
    }
    
    private void createSampleTables() {
        try {
            // Create employees table if it doesn't exist
            String createEmployeesTable = """
                CREATE TABLE IF NOT EXISTS employees (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100),
                    city VARCHAR(50),
                    department VARCHAR(50),
                    salary NUMERIC(10,2),
                    age INTEGER
                )
            """;
            
            jdbcTemplate.execute(createEmployeesTable);
            
            // Insert sample data if table is empty
            String countSql = "SELECT COUNT(*) FROM employees";
            Integer count = jdbcTemplate.queryForObject(countSql, Integer.class);
            
            if (count == 0) {
                String insertSample = """
                    INSERT INTO employees (name, email, city, department, salary, age) VALUES
                    ('John Doe', 'john.doe@company.com', 'Mumbai', 'Engineering', 75000, 30),
                    ('Jane Smith', 'jane.smith@company.com', 'Delhi', 'Marketing', 65000, 28),
                    ('Bob Johnson', 'bob.johnson@company.com', 'Mumbai', 'Engineering', 80000, 35),
                    ('Alice Brown', 'alice.brown@company.com', 'Bangalore', 'HR', 60000, 32),
                    ('Charlie Wilson', 'charlie.wilson@company.com', 'Mumbai', 'Sales', 70000, 29)
                """;
                
                jdbcTemplate.execute(insertSample);
                logger.info("Sample data inserted into employees table");
            }
            
        } catch (Exception e) {
            logger.error("Error creating sample tables: {}", e.getMessage());
        }
    }
    
    public List<String> getAllTableNames() {
        try {
            String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";
            return jdbcTemplate.queryForList(sql, String.class);
        } catch (Exception e) {
            logger.error("Error retrieving table names: {}", e.getMessage());
            return Arrays.asList("employees");
        }
    }
}