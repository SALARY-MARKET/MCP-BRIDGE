package com.mcpbridge.spring;

import com.mcpbridge.spring.config.McpAutoConfiguration;
import com.mcpbridge.spring.config.McpProperties;
import com.mcpbridge.spring.controller.McpController;
import com.mcpbridge.spring.service.McpService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class McpAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(McpAutoConfiguration.class));

    @Test
    void shouldAutoConfigureMcpComponents() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(McpProperties.class);
            assertThat(context).hasSingleBean(McpService.class);
            assertThat(context).hasSingleBean(McpController.class);
        });
    }

    @Test
    void shouldNotConfigureWhenDisabled() {
        contextRunner
                .withPropertyValues("mcp.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(McpController.class);
                });
    }
}