package com.sentinel.sentinel.models;

import com.sentinel.sentinel.enums.IncidentStatus;
import com.sentinel.sentinel.enums.Severity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "incident")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private Severity severity;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private IncidentStatus incidentStatus;
    @Column(name = "service_name")
    private String serviceName;
    @Column(name = "sla_deadline")
    private Instant slaDeadline;
    @Column(name = "sla_violated")
    private boolean slaViolate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Users createdBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_system_integration")
    private SystemIntegration createdBySystemIntegration;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization createdByOrganization;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;

    public Incident(){}

    public Incident(String title,
                    String description,
                    Severity severity,
                    IncidentStatus incidentStatus,
                    String serviceName,
                    Instant slaDeadline,
                    boolean slaViolate,
                    Users createdBy,
                    Organization createdByOrganization,
                    Instant createdAt,
                    Instant updatedAt) {

        this.title = title;
        this.description = description;
        this.severity = severity;
        this.incidentStatus = incidentStatus;
        this.serviceName = serviceName;
        this.slaDeadline = slaDeadline;
        this.slaViolate = slaViolate;
        this.createdBy = createdBy;
        this.createdByOrganization = createdByOrganization;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Incident(
                    Long id,
                    String title,
                    String description,
                    Severity severity,
                    IncidentStatus incidentStatus,
                    String serviceName,
                    Instant slaDeadline,
                    boolean slaViolate,
                    Users createdBy,
                    Organization createdByOrganization,
                    Instant createdAt,
                    Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.incidentStatus = incidentStatus;
        this.serviceName = serviceName;
        this.slaDeadline = slaDeadline;
        this.slaViolate = slaViolate;
        this.createdBy = createdBy;
        this.createdByOrganization = createdByOrganization;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Incident(
            String title,
            String description,
            Severity severity,
            IncidentStatus incidentStatus,
            String serviceName,
            Instant slaDeadline,
            boolean slaViolate,
            SystemIntegration createdBySystemIntegration,
            Organization createdByOrganization,
            Instant createdAt,
            Instant updatedAt) {
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.incidentStatus = incidentStatus;
        this.serviceName = serviceName;
        this.slaDeadline = slaDeadline;
        this.slaViolate = slaViolate;
        this.createdBySystemIntegration = createdBySystemIntegration;
        this.createdByOrganization = createdByOrganization;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Incident incident = (Incident) o;
        return slaViolate == incident.slaViolate && Objects.equals(id, incident.id) && Objects.equals(title, incident.title) && Objects.equals(description, incident.description) && severity == incident.severity && incidentStatus == incident.incidentStatus && Objects.equals(serviceName, incident.serviceName) && Objects.equals(slaDeadline, incident.slaDeadline) && Objects.equals(createdBy, incident.createdBy) && Objects.equals(createdAt, incident.createdAt) && Objects.equals(updatedAt, incident.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, severity, incidentStatus, serviceName, slaDeadline, slaViolate, createdBy, createdAt, updatedAt);
    }

    public Users getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Users createdBy) {
        this.createdBy = createdBy;
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

    public boolean isSlaViolate() {
        return slaViolate;
    }

    public void setSlaViolate(boolean slaViolate) {
        this.slaViolate = slaViolate;
    }

    public Instant getSlaDeadline() {
        return slaDeadline;
    }

    public void setSlaDeadline(Instant slaDeadline) {
        this.slaDeadline = slaDeadline;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public IncidentStatus getIncidentStatus() {
        return incidentStatus;
    }

    public void setIncidentStatus(IncidentStatus incidentStatus) {
        this.incidentStatus = incidentStatus;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SystemIntegration getCreatedBySystemIntegration() {
        return createdBySystemIntegration;
    }

    public void setCreatedBySystemIntegration(SystemIntegration createdBySystemIntegration) {
        this.createdBySystemIntegration = createdBySystemIntegration;
    }

    public Organization getCreatedByOrganization() {
        return createdByOrganization;
    }

    public void setCreatedByOrganization(Organization createdByOrganization) {
        this.createdByOrganization = createdByOrganization;
    }
}
