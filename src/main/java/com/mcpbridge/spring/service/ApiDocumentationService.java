package com.mcpbridge.spring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcpbridge.spring.exception.McpException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ApiDocumentationService {
    
    private static final List<String> HTTP_METHODS = 
        Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH");
    
    private static final String COMPONENTS_SCHEMAS_PREFIX = "#/components/schemas/";
    private static final String DEFAULT_ERROR_MESSAGE = "API documentation is not available. Please ensure OpenAPI endpoint (/v3/api-docs) is enabled.";
    
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    
    private JsonNode openApiDoc;
    private String baseUrl;
    
    public ApiDocumentationService(ObjectMapper objectMapper, 
                                 @Qualifier("mcpRestTemplate") RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }
    
    public void loadOpenApiDoc(String openApiUrl) {
        try {
            String response = restTemplate.getForEntity(openApiUrl, String.class).getBody();
            this.openApiDoc = objectMapper.readTree(response);
            
            // Extract base URL from servers
            JsonNode servers = openApiDoc.get("servers");
            if (servers != null && servers.isArray() && servers.size() > 0) {
                this.baseUrl = servers.get(0).get("url").asText();
            }
        } catch (Exception e) {
            throw new McpException("Failed to load OpenAPI documentation", e);
        }
    }
    
    public String getApiDocumentation(String query) {
        if (openApiDoc == null) {
            return DEFAULT_ERROR_MESSAGE;
        }
        
        StringBuilder response = new StringBuilder();
        response.append("# API Documentation\n\n");
        
        // API 정보
        appendApiInfo(response, openApiDoc.get("info"));
        
        // 질문에 따른 필터링
        String lowerQuery = query.toLowerCase();
        JsonNode paths = openApiDoc.get("paths");
        
        if (paths != null) {
            response.append("## Available Endpoints\n\n");
            
            paths.fields().forEachRemaining(pathEntry -> {
                String path = pathEntry.getKey();
                JsonNode pathItem = pathEntry.getValue();
                
                // 질문과 관련된 엔드포인트만 표시
                if (lowerQuery.contains("all") || 
                    path.toLowerCase().contains(lowerQuery) ||
                    containsRelevantOperation(pathItem, lowerQuery)) {
                    
                    response.append("### ").append(path).append("\n\n");
                    
                    pathItem.fields().forEachRemaining(methodEntry -> {
                        String method = methodEntry.getKey().toUpperCase();
                        JsonNode operation = methodEntry.getValue();
                        
                        if (HTTP_METHODS.contains(method)) {
                            response.append("**").append(method).append("** ");
                            
                            if (operation.has("summary")) {
                                response.append(operation.get("summary").asText());
                            }
                            response.append("\n\n");
                            
                            if (operation.has("description")) {
                                response.append(operation.get("description").asText()).append("\n\n");
                            }
                            
                            // Request body 정보
                            if (operation.has("requestBody")) {
                                response.append("**Request Body:**\n");
                                response.append("```json\n");
                                response.append(getRequestBodyExample(operation.get("requestBody")));
                                response.append("\n```\n\n");
                            }
                            
                            // Response 정보
                            if (operation.has("responses")) {
                                response.append("**Responses:**\n");
                                JsonNode responses = operation.get("responses");
                                responses.fields().forEachRemaining(responseEntry -> {
                                    String statusCode = responseEntry.getKey();
                                    JsonNode responseObj = responseEntry.getValue();
                                    response.append("- ").append(statusCode).append(": ");
                                    if (responseObj.has("description")) {
                                        response.append(responseObj.get("description").asText());
                                    }
                                    
                                    // Response schema 정보 추가
                                    appendResponseSchema(response, responseObj);
                                    response.append("\n");
                                });
                                response.append("\n");
                            }
                        }
                    });
                }
            });
        }
        
        return response.toString();
    }
    
    public String searchEndpoints(String keyword) {
        if (openApiDoc == null) {
            return DEFAULT_ERROR_MESSAGE;
        }
        
        List<String> matchingEndpoints = new ArrayList<>();
        JsonNode paths = openApiDoc.get("paths");
        
        if (paths != null) {
            paths.fields().forEachRemaining(pathEntry -> {
                String path = pathEntry.getKey();
                JsonNode pathItem = pathEntry.getValue();
                
                if (path.toLowerCase().contains(keyword.toLowerCase()) ||
                    containsRelevantOperation(pathItem, keyword.toLowerCase())) {
                    
                    pathItem.fields().forEachRemaining(methodEntry -> {
                        String method = methodEntry.getKey().toUpperCase();
                        JsonNode operation = methodEntry.getValue();
                        
                        if (HTTP_METHODS.contains(method)) {
                            String summary = operation.has("summary") ? 
                                operation.get("summary").asText() : "No description";
                            matchingEndpoints.add(method + " " + path + " - " + summary);
                        }
                    });
                }
            });
        }
        
        if (matchingEndpoints.isEmpty()) {
            return "'" + keyword + "'와 관련된 엔드포인트를 찾을 수 없습니다.";
        }
        
        return "Found endpoints for '" + keyword + "':\n\n" + 
               String.join("\n", matchingEndpoints);
    }
    
    public String getEndpointDetails(String path) {
        if (openApiDoc == null) {
            return DEFAULT_ERROR_MESSAGE;
        }
        
        JsonNode paths = openApiDoc.get("paths");
        if (paths == null || !paths.has(path)) {
            return "엔드포인트 '" + path + "'를 찾을 수 없습니다.";
        }
        
        StringBuilder response = new StringBuilder();
        response.append("# ").append(path).append("\n\n");
        
        JsonNode pathItem = paths.get(path);
        pathItem.fields().forEachRemaining(methodEntry -> {
            String method = methodEntry.getKey().toUpperCase();
            JsonNode operation = methodEntry.getValue();
            
            if (HTTP_METHODS.contains(method)) {
                response.append("## ").append(method).append(" ").append(path).append("\n\n");
                
                if (operation.has("summary")) {
                    response.append("**Summary:** ").append(operation.get("summary").asText()).append("\n\n");
                }
                
                if (operation.has("description")) {
                    response.append("**Description:** ").append(operation.get("description").asText()).append("\n\n");
                }
                
                // Parameters
                if (operation.has("parameters")) {
                    response.append("**Parameters:**\n");
                    JsonNode parameters = operation.get("parameters");
                    parameters.forEach(param -> {
                        response.append("- ").append(param.get("name").asText());
                        response.append(" (").append(param.get("in").asText()).append(")");
                        if (param.has("required") && param.get("required").asBoolean()) {
                            response.append(" *required*");
                        }
                        if (param.has("description")) {
                            response.append(": ").append(param.get("description").asText());
                        }
                        response.append("\n");
                    });
                    response.append("\n");
                }
                
                // Request Body
                if (operation.has("requestBody")) {
                    response.append("**Request Body:**\n");
                    response.append("```json\n");
                    response.append(getRequestBodyExample(operation.get("requestBody")));
                    response.append("\n```\n\n");
                }
                
                // Response 정보
                if (operation.has("responses")) {
                    response.append("**Responses:**\n");
                    JsonNode responses = operation.get("responses");
                    responses.fields().forEachRemaining(responseEntry -> {
                        String statusCode = responseEntry.getKey();
                        JsonNode responseObj = responseEntry.getValue();
                        response.append("- ").append(statusCode).append(": ");
                        if (responseObj.has("description")) {
                            response.append(responseObj.get("description").asText());
                        }
                        
                        // Response schema 정보 추가
                        appendResponseSchema(response, responseObj);
                        response.append("\n");
                    });
                    response.append("\n");
                }
                
                // Example curl command
                response.append("**Example:**\n");
                response.append("```bash\n");
                response.append("curl -X ").append(method).append(" ");
                if (baseUrl != null) {
                    response.append(baseUrl);
                }
                response.append(path);
                if (method.equals("POST") || method.equals("PUT") || method.equals("PATCH")) {
                    response.append(" \\\n  -H 'Content-Type: application/json' \\\n  -d '{...}'");
                }
                response.append("\n```\n\n");
            }
        });
        
        return response.toString();
    }
    
    private boolean containsRelevantOperation(JsonNode pathItem, String keyword) {
        return pathItem.fields().hasNext() && 
               pathItem.toString().toLowerCase().contains(keyword);
    }
    
    private String getRequestBodyExample(JsonNode requestBody) {
        try {
            JsonNode content = requestBody.get("content");
            if (content != null) {
                JsonNode jsonContent = content.get("application/json");
                if (jsonContent != null && jsonContent.has("schema")) {
                    JsonNode schema = jsonContent.get("schema");
                    return generateExampleFromSchema(schema);
                }
            }
        } catch (Exception e) {
            // Fallback to simple example
        }
        return "{\n  \"example\": \"request body\"\n}";
    }
    
    private String generateExampleFromSchema(JsonNode schema) {
        if (schema.has("$ref")) {
            return resolveSchemaReference(schema.get("$ref").asText())
                .map(this::generateObjectExample)
                .orElse("{\n  \"example\": \"request body\"\n}");
        } else if (schema.has("type")) {
            String type = schema.get("type").asText();
            if ("object".equals(type)) {
                return generateObjectExample(schema);
            }
        }
        return "{\n  \"example\": \"request body\"\n}";
    }
    
    private String generateObjectExample(JsonNode schema) {
        StringBuilder example = new StringBuilder("{\n");
        
        if (schema.has("properties")) {
            JsonNode properties = schema.get("properties");
            JsonNode required = schema.get("required");
            Set<String> requiredFields = new HashSet<>();
            
            if (required != null && required.isArray()) {
                required.forEach(field -> requiredFields.add(field.asText()));
            }
            
            properties.fields().forEachRemaining(prop -> {
                String fieldName = prop.getKey();
                JsonNode fieldSchema = prop.getValue();
                
                example.append("  \"").append(fieldName).append("\": ");
                example.append(getExampleValue(fieldSchema));
                
                if (requiredFields.contains(fieldName)) {
                    example.append(", // required");
                }
                example.append("\n");
            });
        }
        
        example.append("}");
        return example.toString();
    }
    
    private String getExampleValue(JsonNode fieldSchema) {
        if (fieldSchema.has("type")) {
            String type = fieldSchema.get("type").asText();
            switch (type) {
                case "string":
                    if (fieldSchema.has("pattern")) {
                        String pattern = fieldSchema.get("pattern").asText();
                        if (pattern.contains("[0-9]")) {
                            return "\"01012345678\"";
                        }
                    }
                    return "\"example string\"";
                case "integer":
                    return "123";
                case "number":
                    return "123.45";
                case "boolean":
                    return "true";
                case "array":
                    return "[\"item1\", \"item2\"]";
                default:
                    return "\"example\"";
            }
        }
        return "\"example\"";
    }
    
    private String generateResponseExample(JsonNode schema) {
        try {
            // Handle allOf structures
            if (schema.has("allOf")) {
                schema = schema.get("allOf").get(0);
            }
            
            if (schema.has("$ref")) {
                return resolveSchemaReference(schema.get("$ref").asText())
                    .map(this::generateObjectExample)
                    .orElse("Response OK");
            } else if (schema.has("type")) {
                String type = schema.get("type").asText();
                if ("array".equals(type) && schema.has("items")) {
                    JsonNode items = schema.get("items");
                    String itemExample = generateResponseExample(items);
                    return "[\n  " + itemExample + "\n]";
                } else if ("object".equals(type)) {
                    return generateObjectExample(schema);
                } else {
                    return getExampleValue(schema);
                }
            }
        } catch (Exception e) {
            // Ignore and return empty
        }
        return "Response OK";
    }
    
    private Optional<JsonNode> getJsonContent(JsonNode contentNode) {
        if (contentNode == null) return Optional.empty();
        
        if (contentNode.has("application/json")) {
            return Optional.of(contentNode.get("application/json"));
        } else if (contentNode.has("*/*")) {
            return Optional.of(contentNode.get("*/*"));
        }
        return Optional.empty();
    }
    
    private Optional<JsonNode> resolveSchemaReference(String ref) {
        if (!ref.startsWith(COMPONENTS_SCHEMAS_PREFIX)) {
            return Optional.empty();
        }
        
        String schemaName = ref.substring(COMPONENTS_SCHEMAS_PREFIX.length());
        JsonNode components = openApiDoc.get("components");
        
        return Optional.ofNullable(components)
            .filter(c -> c.has("schemas"))
            .map(c -> c.get("schemas"))
            .filter(schemas -> schemas.has(schemaName))
            .map(schemas -> schemas.get(schemaName));
    }
    
    private void appendIfPresent(StringBuilder sb, String prefix, JsonNode node, String fieldName) {
        if (node != null && node.has(fieldName)) {
            JsonNode fieldNode = node.get(fieldName);
            if (fieldNode != null && !fieldNode.isNull()) {
                sb.append(prefix).append(fieldNode.asText()).append("\n");
            }
        }
    }
    
    private void appendApiInfo(StringBuilder response, JsonNode info) {
        if (info == null) {
            return;
        }
        
        appendIfPresent(response, "**Title:** ", info, "title");
        appendIfPresent(response, "**Version:** ", info, "version");
        appendIfPresent(response, "**Description:** ", info, "description");
        response.append("\n");
    }
    
    private void appendResponseSchema(StringBuilder response, JsonNode responseObj) {
        if (responseObj == null || !responseObj.has("content")) {
            return;
        }
        
        getJsonContent(responseObj.get("content"))
            .filter(jsonContent -> jsonContent.has("schema"))
            .map(jsonContent -> jsonContent.get("schema"))
            .map(this::generateResponseExample)
            .filter(example -> !example.isEmpty() && !"Response OK".equals(example))
            .ifPresent(example -> 
                response.append("\n```json\n").append(example).append("\n```")
            );
    }
}