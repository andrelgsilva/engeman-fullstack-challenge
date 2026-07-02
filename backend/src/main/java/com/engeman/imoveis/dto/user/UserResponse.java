package com.engeman.imoveis.dto.user;

import com.engeman.imoveis.entity.User;
import com.engeman.imoveis.enums.Role;

import java.time.LocalDateTime;

public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.active = user.isActive();
        this.createdAt = user.getCreatedAt();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}