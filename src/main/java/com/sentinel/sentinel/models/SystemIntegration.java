package com.sentinel.sentinel.models;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "system_integration")
public class SystemIntegration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "client_id", unique = true)
    private String clientId;
    @Column(name = "client_secret_hash")
    private String clientSecretHash;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organizationId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Users createdBy;
    private boolean active;
    @Column(name = "last_used_at")
    private Instant lastUsedAt;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;

    public SystemIntegration(Long id, String name, String clientId, String clientSecretHash, Organization organizationId,
                             Users createdBy, boolean active, Instant lastUsedAt, Instant updatedAt, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.clientId = clientId;
        this.clientSecretHash = clientSecretHash;
        this.organizationId = organizationId;
        this.createdBy = createdBy;
        this.active = active;
        this.lastUsedAt = lastUsedAt;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecretHash() {
        return clientSecretHash;
    }

    public void setClientSecretHash(String clientSecretHash) {
        this.clientSecretHash = clientSecretHash;
    }

    public Organization getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Organization organizationId) {
        this.organizationId = organizationId;
    }

    public Users getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Users createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(Instant lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SystemIntegration that = (SystemIntegration) o;
        return active == that.active && Objects.equals(id, that.id) && Objects.equals(name, that.name) &&
                Objects.equals(clientId, that.clientId) && Objects.equals(clientSecretHash, that.clientSecretHash)
                && Objects.equals(organizationId, that.organizationId) && Objects.equals(createdBy, that.createdBy)
                && Objects.equals(lastUsedAt, that.lastUsedAt) && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, clientId, clientSecretHash, organizationId, createdBy, active, lastUsedAt,
                createdAt, updatedAt);
    }
}
