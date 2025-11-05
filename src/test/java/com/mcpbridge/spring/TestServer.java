package com.mcpbridge.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
public class TestServer {
    
    public static void main(String[] args) {
        SpringApplication.run(TestServer.class, args);
    }
    
    @RestController
    @RequestMapping("/api/users")
    public static class UserController {
        
        @GetMapping("/{id}")
        public User getUser(@PathVariable Long id) {
            return new User(id, "John Doe", "john@example.com");
        }
        
        @PostMapping
        public User createUser(@RequestBody CreateUserRequest request) {
            return new User(1L, request.name, request.email);
        }
    }
    
    public static class User {
        public Long id;
        public String name;
        public String email;
        
        public User(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }
    
    public static class CreateUserRequest {
        public String name;
        public String email;
    }
}