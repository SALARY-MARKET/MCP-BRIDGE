package com.mcpbridge.spring.service;

import com.mcpbridge.spring.model.McpTool;
import com.mcpbridge.spring.exception.McpException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class McpService {
    
    private final ApiDocumentationService apiDocService;
    private final ObjectMapper objectMapper;
    
    public McpService(ApiDocumentationService apiDocService, ObjectMapper objectMapper) {
        this.apiDocService = apiDocService;
        this.objectMapper = objectMapper;
    }
    
    public List<McpTool> getTools(String openApiUrl) {
        // OpenAPI 문서 로드
        apiDocService.loadOpenApiDoc(openApiUrl);
        List<McpTool> tools = new ArrayList<>();
        
        // API 문서화 전용 도구들
        tools.add(new McpTool(
            "getApiDocumentation",
            "Get comprehensive API documentation and usage examples",
            Map.of(
                "type", "object",
                "properties", Map.of(
                    "query", Map.of("type", "string", "description", "API related question (e.g., 'How to create user?', 'What endpoints are available?')")
                ),
                "required", List.of("query")
            )
        ));
        
        tools.add(new McpTool(
            "searchEndpoints",
            "Search for specific API endpoints and their details",
            Map.of(
                "type", "object",
                "properties", Map.of(
                    "keyword", Map.of("type", "string", "description", "Search keyword (e.g., 'user', 'order', 'payment')")
                ),
                "required", List.of("keyword")
            )
        ));
        
        tools.add(new McpTool(
            "getEndpointDetails",
            "Get detailed information about a specific API endpoint",
            Map.of(
                "type", "object",
                "properties", Map.of(
                    "path", Map.of("type", "string", "description", "API endpoint path (e.g., '/api/users/{id}')")
                ),
                "required", List.of("path")
            )
        ));
        
        return tools;
    }
    
    public Object executeBusinessLogic(String toolName, Map<String, Object> arguments) {
        try {
            if (arguments == null) {
                arguments = new HashMap<>();
            }
            
            switch (toolName) {
                case "getApiDocumentation":
                    String query = (String) arguments.getOrDefault("query", "all");
                    return apiDocService.getApiDocumentation(query);
                case "searchEndpoints":
                    String keyword = (String) arguments.getOrDefault("keyword", "api");
                    return apiDocService.searchEndpoints(keyword);
                case "getEndpointDetails":
                    String path = (String) arguments.getOrDefault("path", "/api");
                    return apiDocService.getEndpointDetails(path);
                default:
                    return "Unknown tool: " + toolName;
            }
        } catch (Exception e) {
            throw new McpException("Failed to execute API documentation logic: " + e.getMessage(), e);
        }
    }
    
    public Object executeBusinessLogic(String toolName, Map<String, Object> arguments, String openApiUrl) {
        try {
            if (arguments == null) {
                arguments = new HashMap<>();
            }
            
            // OpenAPI 문서 로드
            apiDocService.loadOpenApiDoc(openApiUrl);
            
            switch (toolName) {
                case "getApiDocumentation":
                    String query = (String) arguments.getOrDefault("query", "all");
                    return apiDocService.getApiDocumentation(query);
                case "searchEndpoints":
                    String keyword = (String) arguments.getOrDefault("keyword", "api");
                    return apiDocService.searchEndpoints(keyword);
                case "getEndpointDetails":
                    String path = (String) arguments.getOrDefault("path", "/api");
                    return apiDocService.getEndpointDetails(path);
                default:
                    return "Unknown tool: " + toolName;
            }
        } catch (Exception e) {
            throw new McpException("Failed to execute API documentation logic: " + e.getMessage(), e);
        }
    }
    
    private Map<String, Object> createInputSchema(JsonNode operation, String method, String path) {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("method", Map.of("type", "string", "default", method.toUpperCase()));
        properties.put("path", Map.of("type", "string", "default", path));
        
        List<String> required = new ArrayList<>();
        required.add("path");
        
        // Add request body schema for POST/PUT/PATCH
        if (Arrays.asList("post", "put", "patch").contains(method.toLowerCase())) {
            JsonNode requestBody = operation.get("requestBody");
            if (requestBody != null) {
                properties.put("body", Map.of("type", "object", "description", "Request body"));
            }
        }
        
        // Add query parameters
        JsonNode parameters = operation.get("parameters");
        if (parameters != null && parameters.isArray()) {
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("type", "object");
            queryParams.put("description", "Query parameters");
            properties.put("queryParams", queryParams);
        }
        
        schema.put("properties", properties);
        schema.put("required", required);
        
        return schema;
    }
}