package com.mcpbridge.spring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mcp")
public class McpProperties {
    
    private boolean enabled = true;
    private String basePath = "/mcp";
    private String openApiPath = "/v3/api-docs";
    private String token;
    
    public McpProperties() {}
    
    public McpProperties(boolean enabled, String basePath, String openApiPath, String token) {
        this.enabled = enabled;
        this.basePath = basePath;
        this.openApiPath = openApiPath;
        this.token = token;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getBasePath() {
        return basePath;
    }
    
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
    
    public String getOpenApiPath() {
        return openApiPath;
    }
    
    public void setOpenApiPath(String openApiPath) {
        this.openApiPath = openApiPath;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
}