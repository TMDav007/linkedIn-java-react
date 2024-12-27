package com.linkedIn.backend.features.authentication.dto;

public class AuthenticationResponseBody {
    private final String token;
    private final String message;


    public AuthenticationResponseBody(String token, String message) {
        this.token = token;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}
