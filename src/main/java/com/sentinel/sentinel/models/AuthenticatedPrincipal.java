package com.sentinel.sentinel.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

public class AuthenticatedPrincipal implements UserDetails {

    private final String id;
    private final String username;
    private final String password;
    private final String type;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Long organizationId;

    public AuthenticatedPrincipal(String id,
                                  String username,
                                  String password,
                                  String type,
                                  Collection<? extends GrantedAuthority> authorities,
                                  Long organizationId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.type = type;
        this.authorities = authorities;
        this.organizationId = organizationId;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public boolean isSystem() {
        return Objects.equals(type, "system");
    }

    public boolean isUser() {
        return Objects.equals(type, "user");
    }

    public boolean hasRole(String role) {
        return authorities.stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), role));
    }

    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    public boolean isTech() {
        return hasRole("ROLE_TECH");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {return password;}

    @Override
    public String getUsername() {
        return username;
    }
}