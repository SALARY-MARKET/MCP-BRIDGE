package com.mcpbridge.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcpbridge.spring.security.McpBridgeAuthFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(McpProperties.class)
@ConditionalOnProperty(prefix = "mcp", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.mcpbridge.spring")
public class McpAutoConfiguration {
    
    @Bean
    @Qualifier("mcpRestTemplate")
    @ConditionalOnMissingBean(name = "mcpRestTemplate")
    public RestTemplate mcpRestTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    

    @Bean
    @ConditionalOnProperty(prefix = "mcp", name = "token")
    public FilterRegistrationBean<McpBridgeAuthFilter> mcpAuthFilter(McpProperties mcpProperties) {
        FilterRegistrationBean<McpBridgeAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new McpBridgeAuthFilter(mcpProperties));
        registration.addUrlPatterns(mcpProperties.getBasePath() + "/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setName("mcpBridgeAuthFilter");
        return registration;
    }
}