package com.nounou.times.dto;

import com.nounou.times.model.Nounou;

public class AuthResponse {
    private String token;
    private NounouDto nounou;

    public AuthResponse(String token, Nounou nounou) {
        this.token = token;
        this.nounou = new NounouDto(nounou);
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public NounouDto getNounou() { return nounou; }
    public void setNounou(NounouDto nounou) { this.nounou = nounou; }
}
