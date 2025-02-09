package com.store.dto;

public class JwtRequest {
    private String username;
    private String password;

    public JwtRequest() {
    }

    public JwtRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public String toString() {
        return "JwtRequest(username=" + this.getUsername() + ", password=" + this.getPassword() + ")";
    }
}