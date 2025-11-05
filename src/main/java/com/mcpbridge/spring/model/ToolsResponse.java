package com.mcpbridge.spring.model;

import java.util.List;
import java.util.Map;

public class ToolsResponse {
    private List<McpTool> tools;
    private Map<String, Object> meta;
    
    public ToolsResponse(List<McpTool> tools, Map<String, Object> meta) {
        this.tools = tools;
        this.meta = meta;
    }
    
    public List<McpTool> getTools() {
        return tools;
    }
    
    public Map<String, Object> getMeta() {
        return meta;
    }
}