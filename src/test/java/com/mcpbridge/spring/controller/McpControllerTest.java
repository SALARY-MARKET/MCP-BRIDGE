package com.mcpbridge.spring.controller;

import com.mcpbridge.spring.config.McpProperties;
import com.mcpbridge.spring.model.McpCallRequest;
import com.mcpbridge.spring.service.McpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(McpController.class)
class McpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private McpService mcpService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public McpProperties mcpProperties() {
            return new McpProperties(true, "/mcp", "/v3/api-docs", null);
        }
    }

    @Test
    void shouldReturnTools() throws Exception {
        when(mcpService.getTools(anyString())).thenReturn(List.of());

        mockMvc.perform(get("/mcp/tools"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tools").isArray())
                .andExpect(jsonPath("$.meta").exists());
    }

    @Test
    void shouldExecuteTool() throws Exception {
        when(mcpService.executeBusinessLogic(eq("getApiDocumentation"), any(), anyString()))
                .thenReturn("API documentation result");

        McpCallRequest request = new McpCallRequest("getApiDocumentation", Map.of("query", "all"));

        mockMvc.perform(post("/mcp/call")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.result").value("API documentation result"));
    }

    @Test
    void shouldReturnErrorForInvalidToolName() throws Exception {
        McpCallRequest request = new McpCallRequest("", Map.of());

        mockMvc.perform(post("/mcp/call")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }
}