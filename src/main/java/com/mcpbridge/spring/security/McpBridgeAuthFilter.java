package com.mcpbridge.spring.security;

import com.mcpbridge.spring.config.McpProperties;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class McpBridgeAuthFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(McpBridgeAuthFilter.class);
    
    private final McpProperties mcpProperties;
    
    public McpBridgeAuthFilter(McpProperties mcpProperties) {
        this.mcpProperties = mcpProperties;
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestPath = httpRequest.getRequestURI();
        String clientIP = getClientIP(httpRequest);
        
        // MCP 경로가 아니면 통과 (정밀한 경로 매칭)
        if (!requestPath.equals(mcpProperties.getBasePath()) && 
            !requestPath.startsWith(mcpProperties.getBasePath() + "/")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Authorization 헤더 확인
        String authHeader = httpRequest.getHeader("Authorization");
        String expectedToken = mcpProperties.getToken();
        
        if (!StringUtils.hasText(expectedToken)) {
            logger.info("MCP Bridge token not configured, skipping authentication");
            chain.doFilter(request, response);
            return;
        }
        
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            logger.info("Unauthorized MCP access attempt from IP: {}", clientIP);
            writeError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, 
                      "Authorization header required");
            return;
        }
        
        String providedToken = authHeader.substring(7); // "Bearer " 제거
        
        if (!expectedToken.equals(providedToken)) {
            logger.info("Invalid MCP token attempt from IP: {}", clientIP);
            writeError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, 
                      "Invalid token");
            return;
        }
        
        logger.info("Authorized MCP access from IP: {}", clientIP);
        chain.doFilter(request, response);
    }
    
    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIP)) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
}