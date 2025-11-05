package com.mcpbridge.spring.integration;

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
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
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
        
        @GetMapping
        @Operation(summary = "List all users", description = "Get a list of all users")
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
        public java.util.List<User> getAllUsers() {
            return java.util.Arrays.asList(
                new User(1L, "John Doe", "john@example.com"),
                new User(2L, "Jane Smith", "jane@example.com")
            );
        }
        
        @DeleteMapping("/{id}")
        @Operation(summary = "Delete user", description = "Delete a user by their ID")
        @ApiResponse(responseCode = "204", description = "User deleted successfully")
        @ApiResponse(responseCode = "404", description = "User not found")
        public void deleteUser(@Parameter(description = "User ID") @PathVariable Long id) {
            // Delete logic here
        }
    }
    
    @RestController
    @RequestMapping("/api/orders")
    @Tag(name = "Order Management", description = "APIs for managing orders")
    public static class OrderController {
        
        @GetMapping("/{id}")
        @Operation(summary = "Get order by ID", description = "Retrieve an order by its unique identifier")
        @ApiResponse(responseCode = "200", description = "Order found",
                    content = @Content(schema = @Schema(implementation = Order.class)))
        public Order getOrder(@Parameter(description = "Order ID") @PathVariable Long id) {
            return new Order(id, "ORD-001", 99.99);
        }
        
        @PostMapping
        @Operation(summary = "Create new order", description = "Create a new order")
        @ApiResponse(responseCode = "201", description = "Order created successfully",
                    content = @Content(schema = @Schema(implementation = Order.class)))
        public Order createOrder(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Order creation request", required = true,
                content = @Content(schema = @Schema(implementation = CreateOrderRequest.class))
        ) @RequestBody CreateOrderRequest request) {
            return new Order(1L, request.orderNumber, request.amount);
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
    
    @Schema(description = "Order entity")
    public static class Order {
        @Schema(description = "Order ID", example = "1")
        public Long id;
        
        @Schema(description = "Order number", example = "ORD-001")
        public String orderNumber;
        
        @Schema(description = "Order amount", example = "99.99")
        public Double amount;
        
        public Order(Long id, String orderNumber, Double amount) {
            this.id = id;
            this.orderNumber = orderNumber;
            this.amount = amount;
        }
    }
    
    @Schema(description = "Request to create a new order")
    public static class CreateOrderRequest {
        @Schema(description = "Order number", example = "ORD-001", required = true)
        public String orderNumber;
        
        @Schema(description = "Order amount", example = "99.99", required = true)
        public Double amount;
    }
}