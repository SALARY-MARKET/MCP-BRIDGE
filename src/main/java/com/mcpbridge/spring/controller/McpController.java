package com.mcpbridge.spring.controller;

import com.mcpbridge.spring.config.McpProperties;
import com.mcpbridge.spring.exception.McpException;
import com.mcpbridge.spring.model.*;
import com.mcpbridge.spring.service.McpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("${mcp.base-path:/mcp}")
public class McpController {
    
    private static final Logger log = LoggerFactory.getLogger(McpController.class);
    
    private final McpService mcpService;
    private final McpProperties mcpProperties;
    private volatile String cachedOpenApiUrl;
    
    public McpController(McpService mcpService, McpProperties mcpProperties) {
        this.mcpService = mcpService;
        this.mcpProperties = mcpProperties;
    }
    
    @PostConstruct
    public void init() {
        log.info("Initializing MCP Controller with base path: {}", mcpProperties.getBasePath());
    }
    
    @GetMapping("/tools")
    public ResponseEntity<ToolsResponse> getTools(HttpServletRequest request) {
        try {
            String openApiUrl = getOrCacheOpenApiUrl(request);
            List<McpTool> tools = mcpService.getTools(openApiUrl);
            
            Map<String, Object> meta = Map.of(
                "description", "Auto-generated MCP tools from OpenAPI specification",
                "version", "1.0.0"
            );
            
            return ResponseEntity.ok(new ToolsResponse(tools, meta));
            
        } catch (McpException e) {
            log.error("Failed to get MCP tools: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            log.error("Unexpected error getting tools", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/call")
    public ResponseEntity<CallResponse> callTool(@RequestBody McpCallRequest request, HttpServletRequest httpRequest) {
        try {
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(CallResponse.error("Tool name is required"));
            }
            
            String openApiUrl = getOrCacheOpenApiUrl(httpRequest);
            Map<String, Object> arguments = Optional.ofNullable(request.getArguments()).orElse(Map.of());
            
            Object result = mcpService.executeBusinessLogic(request.getName(), arguments, openApiUrl);
            return ResponseEntity.ok(CallResponse.success(result));
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for tool '{}': {}", request.getName(), e.getMessage());
            return ResponseEntity.badRequest().body(CallResponse.error(e.getMessage()));
            
        } catch (McpException e) {
            log.error("MCP error executing tool '{}': {}", request.getName(), e.getMessage(), e);
            return ResponseEntity.internalServerError().body(CallResponse.error("Failed to execute tool"));
            
        } catch (Exception e) {
            log.error("Unexpected error executing tool '{}'", request.getName(), e);
            return ResponseEntity.internalServerError().body(CallResponse.error("Internal server error"));
        }
    }
    
    private String getOrCacheOpenApiUrl(HttpServletRequest request) {
        if (cachedOpenApiUrl == null) {
            synchronized (this) {
                if (cachedOpenApiUrl == null) {
                    String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                            .replacePath(request.getContextPath())
                            .replaceQuery(null)
                            .build()
                            .toUriString();
                    cachedOpenApiUrl = baseUrl + mcpProperties.getOpenApiPath();
                    log.debug("Cached OpenAPI URL: {}", cachedOpenApiUrl);
                }
            }
        }
        return cachedOpenApiUrl;
    }
}