package com.nounou.times.dto;

import com.nounou.times.model.Nounou;

public class AuthResponse {
    private String token;
    private Nounou nounou;

    public AuthResponse(String token, Nounou nounou) {
        this.token = token;
        this.nounou = nounou;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Nounou getNounou() { return nounou; }
    public void setNounou(Nounou nounou) { this.nounou = nounou; }
}
