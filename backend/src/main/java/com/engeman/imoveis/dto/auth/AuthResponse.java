package com.engeman.imoveis.dto.auth;

import com.engeman.imoveis.enums.Role;

public class AuthResponse {

    private String token;
    private String name;
    private String email;
    private Role role;

    public AuthResponse(String token, String name, String email, Role role) {
        this.token = token;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getToken() { return token; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
}