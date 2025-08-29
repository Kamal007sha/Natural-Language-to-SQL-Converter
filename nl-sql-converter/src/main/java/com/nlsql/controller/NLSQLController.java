package com.nlsql.controller;

import com.nlsql.model.QueryRequest;
import com.nlsql.model.QueryResponse;
import com.nlsql.service.NLSQLConverterService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/query")
@CrossOrigin(origins = "*")
public class NLSQLController {
    
    private static final Logger logger = LoggerFactory.getLogger(NLSQLController.class);
    
    @Autowired
    private NLSQLConverterService nlsqlConverterService;
    
    @PostMapping
    public ResponseEntity<QueryResponse> executeQuery(@Valid @RequestBody QueryRequest request) {
        logger.info("Received query: {}", request.getQuery());
        
        try {
            long startTime = System.currentTimeMillis();
            QueryResponse response = nlsqlConverterService.processQuery(request.getQuery());
            long endTime = System.currentTimeMillis();
            
            response.setExecutionTimeMs(endTime - startTime);
            logger.info("Query processed successfully in {} ms", response.getExecutionTimeMs());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error processing query: {}", e.getMessage(), e);
            QueryResponse errorResponse = new QueryResponse(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("NL-SQL Converter is running");
    }
}