package com.sentinel.sentinel.models;

import com.sentinel.sentinel.enums.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class Users implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    private Roles role;

    private int status;


    public Users() {

    }

    public Users(@NotNull Long id, @NotBlank String name, @NotBlank String email, String encodedPassword,
                 Organization organization, Roles role, int status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = encodedPassword;
        this.organization = organization;
        this.role = role;
        this.status = 1;
    }

    public Users(@NotBlank String name, @NotBlank String email, String encodedPassword, Organization organization,
                 Roles role, int status) {
        this.name = name;
        this.email = email;
        this.passwordHash = encodedPassword;
        this.organization = organization;
        this.role = role;
        this.status = 1;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if(this.role == Roles.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_TECH"), new SimpleGrantedAuthority("ROLE_USER"));
        } else if (this.role == Roles.TECH) {
            return List.of(new SimpleGrantedAuthority("ROLE_TECH"), new SimpleGrantedAuthority("ROLE_USER"));
        }

        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public @Nullable String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        role = role;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return status == users.status && Objects.equals(id, users.id) && Objects.equals(name, users.name)
                && Objects.equals(email, users.email) && Objects.equals(passwordHash, users.passwordHash)
                && Objects.equals(organization, users.organization) && role == users.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, passwordHash, organization, role, status);
    }
}
