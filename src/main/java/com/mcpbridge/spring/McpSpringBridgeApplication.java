package com.mcpbridge.spring;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
public class McpSpringBridgeApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(McpSpringBridgeApplication.class, args);
    }
    
    @RestController
    @RequestMapping("/api/users")
    @Tag(name = "User Management", description = "APIs for managing users")
    public static class UserController {
        
        @GetMapping("/{id}")
        @Operation(summary = "Get user by ID", description = "Retrieve a user by their unique identifier")
        @ApiResponse(responseCode = "200", description = "User found", 
                    content = @Content(schema = @Schema(implementation = User.class)))
        @ApiResponse(responseCode = "404", description = "User not found")
        public User getUser(@Parameter(description = "User ID") @PathVariable Long id) {
            return new User(id, "John Doe", "john@example.com");
        }
        
        @PostMapping
        @Operation(summary = "Create new user", description = "Create a new user with the provided information")
        @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = User.class)))
        @ApiResponse(responseCode = "400", description = "Invalid input")
        public User createUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User creation request", required = true,
                content = @Content(schema = @Schema(implementation = CreateUserRequest.class))
        ) @RequestBody CreateUserRequest request) {
            return new User(1L, request.name, request.email);
        }
    }
    
    @Schema(description = "User entity")
    public static class User {
        @Schema(description = "User ID", example = "1")
        public Long id;
        
        @Schema(description = "User name", example = "John Doe")
        public String name;
        
        @Schema(description = "User email", example = "john@example.com")
        public String email;
        
        public User(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }
    
    @Schema(description = "Request to create a new user")
    public static class CreateUserRequest {
        @Schema(description = "User name", example = "John Doe", required = true)
        public String name;
        
        @Schema(description = "User email", example = "john@example.com", required = true)
        public String email;
    }
}