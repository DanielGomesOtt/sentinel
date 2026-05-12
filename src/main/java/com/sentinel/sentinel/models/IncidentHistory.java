package com.sentinel.sentinel.models;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "incident_history")
public class IncidentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id")
    private Incident incidentId;
    @Column(name = "previous_status")
    private String previousStatus;
    @Column(name = "new_status")
    private String newStatus;
    private String action;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private Users performedBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_system_integration")
    private SystemIntegration performedBySystemIntegration;
    @Column(name = "created_at")
    private Instant createdAt;

    public IncidentHistory(Incident incidentId, String previousStatus, String newStatus, String action, Users performedBy, Instant createdAt) {
        this.incidentId = incidentId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.action = action;
        this.performedBy = performedBy;
        this.createdAt = createdAt;
    }

    public IncidentHistory(Incident incidentId, String previousStatus, String newStatus, String action, SystemIntegration performedBySystemIntegration, Instant createdAt) {
        this.incidentId = incidentId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.action = action;
        this.performedBySystemIntegration = performedBySystemIntegration;
        this.createdAt = createdAt;
    }

    public IncidentHistory() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Incident getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(Incident incidentId) {
        this.incidentId = incidentId;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Users getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(Users performedBy) {
        this.performedBy = performedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public SystemIntegration getPerformedBySystemIntegration() {
        return performedBySystemIntegration;
    }

    public void setPerformedBySystemIntegration(SystemIntegration performedBySystemIntegration) {
        this.performedBySystemIntegration = performedBySystemIntegration;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IncidentHistory that = (IncidentHistory) o;
        return Objects.equals(id, that.id) && Objects.equals(incidentId, that.incidentId) && Objects.equals(previousStatus, that.previousStatus) && Objects.equals(newStatus, that.newStatus) && Objects.equals(action, that.action) && Objects.equals(performedBy, that.performedBy) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, incidentId, previousStatus, newStatus, action, performedBy, createdAt);
    }
}
