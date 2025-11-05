package com.mcpbridge.spring;

import com.mcpbridge.spring.model.CallResponse;
import com.mcpbridge.spring.model.McpCallRequest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleTest {

    @Test
    void shouldCreateMcpCallRequest() {
        McpCallRequest request = new McpCallRequest("testTool", Map.of("key", "value"));
        
        assertThat(request.getName()).isEqualTo("testTool");
        assertThat(request.getArguments()).containsEntry("key", "value");
    }

    @Test
    void shouldCreateSuccessResponse() {
        CallResponse response = CallResponse.success("test result");
        
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getResult()).isEqualTo("test result");
        assertThat(response.getError()).isNull();
    }

    @Test
    void shouldCreateErrorResponse() {
        CallResponse response = CallResponse.error("test error");
        
        assertThat(response.getStatus()).isEqualTo("error");
        assertThat(response.getError()).isEqualTo("test error");
        assertThat(response.getResult()).isNull();
    }
}