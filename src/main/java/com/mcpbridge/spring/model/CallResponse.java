package com.mcpbridge.spring.model;

public class CallResponse {
    private Object result;
    private String status;
    private String error;
    
    public CallResponse(Object result, String status) {
        this.result = result;
        this.status = status;
    }
    
    public CallResponse(String status, String error) {
        this.status = status;
        this.error = error;
    }
    
    public static CallResponse success(Object result) {
        return new CallResponse(result, "success");
    }
    
    public static CallResponse error(String message) {
        return new CallResponse("error", message);
    }
    
    public Object getResult() {
        return result;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getError() {
        return error;
    }
}