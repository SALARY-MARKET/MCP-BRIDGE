package com.mcpbridge.spring.service;

import com.mcpbridge.spring.exception.McpException;
import com.mcpbridge.spring.model.McpTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class McpServiceTest {

    @Mock
    private ApiDocumentationService apiDocService;

    private McpService mcpService;

    @BeforeEach
    void setUp() {
        mcpService = new McpService(apiDocService, new ObjectMapper());
    }

    @Test
    void shouldReturnThreeTools() {
        List<McpTool> tools = mcpService.getTools("http://localhost:8080/v3/api-docs");

        assertThat(tools).hasSize(3);
        assertThat(tools).extracting(McpTool::getName)
                .containsExactly("getApiDocumentation", "searchEndpoints", "getEndpointDetails");
    }

    @Test
    void shouldExecuteGetApiDocumentation() {
        when(apiDocService.getApiDocumentation("all")).thenReturn("API docs");

        Object result = mcpService.executeBusinessLogic("getApiDocumentation", 
                Map.of("query", "all"), "http://localhost:8080/v3/api-docs");

        assertThat(result).isEqualTo("API docs");
    }

    @Test
    void shouldExecuteSearchEndpoints() {
        when(apiDocService.searchEndpoints("user")).thenReturn("User endpoints");

        Object result = mcpService.executeBusinessLogic("searchEndpoints", 
                Map.of("keyword", "user"), "http://localhost:8080/v3/api-docs");

        assertThat(result).isEqualTo("User endpoints");
    }

    @Test
    void shouldExecuteGetEndpointDetails() {
        when(apiDocService.getEndpointDetails("/api/users")).thenReturn("Endpoint details");

        Object result = mcpService.executeBusinessLogic("getEndpointDetails", 
                Map.of("path", "/api/users"), "http://localhost:8080/v3/api-docs");

        assertThat(result).isEqualTo("Endpoint details");
    }

    @Test
    void shouldReturnUnknownToolMessage() {
        Object result = mcpService.executeBusinessLogic("unknownTool", 
                Map.of(), "http://localhost:8080/v3/api-docs");

        assertThat(result).isEqualTo("Unknown tool: unknownTool");
    }

    @Test
    void shouldThrowMcpExceptionOnServiceError() {
        when(apiDocService.getApiDocumentation(anyString())).thenThrow(new RuntimeException("Service error"));

        assertThatThrownBy(() -> mcpService.executeBusinessLogic("getApiDocumentation", 
                Map.of("query", "all"), "http://localhost:8080/v3/api-docs"))
                .isInstanceOf(McpException.class)
                .hasMessageContaining("Failed to execute API documentation logic");
    }
}