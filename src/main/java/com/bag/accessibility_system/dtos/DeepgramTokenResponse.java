package com.bag.accessibility_system.dtos;

public class DeepgramTokenResponse {
    private String token;

    public DeepgramTokenResponse() {
    }

    public DeepgramTokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
