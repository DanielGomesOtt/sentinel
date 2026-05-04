package com.sentinel.sentinel.enums;

public enum Roles {
    ADMIN("admin"),
    TECH("tech"),
    USER("user"),
    SYSTEM("system");

    private String role;

    Roles(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
