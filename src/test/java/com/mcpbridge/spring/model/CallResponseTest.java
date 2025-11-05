package com.mcpbridge.spring.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CallResponseTest {

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