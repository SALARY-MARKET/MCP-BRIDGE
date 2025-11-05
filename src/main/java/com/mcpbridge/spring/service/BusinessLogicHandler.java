package com.mcpbridge.spring.service;

import java.util.Map;

public interface BusinessLogicHandler {
    Object handle(Map<String, Object> arguments);
}