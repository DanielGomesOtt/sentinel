package com.sentinel.sentinel.models;

import com.sentinel.sentinel.enums.IncidentLogLevel;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "incident_log")
public class IncidentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id")
    private Incident incidentId;
    @Enumerated(EnumType.STRING)
    private IncidentLogLevel level;
    private String message;
    @Column(name = "stack_trace")
    private String stackTrace;
    @Column(name = "service_name")
    private String serviceName;
    @Column(name = "created_at")
    private Instant createdAt;

    public IncidentLog() {}

    public IncidentLog(Incident incidentId, IncidentLogLevel level, String message, String stacktrace, String serviceName, Instant createdAt) {
        this.incidentId = incidentId;
        this.level = level;
        this.message = message;
        this.stackTrace = stacktrace;
        this.serviceName = serviceName;
        this.createdAt = createdAt;
    }

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

    public IncidentLogLevel getLevel() {
        return level;
    }

    public void setLevel(IncidentLogLevel level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IncidentLog that = (IncidentLog) o;
        return Objects.equals(id, that.id) && Objects.equals(incidentId, that.incidentId) && level == that.level && Objects.equals(message, that.message) && Objects.equals(stackTrace, that.stackTrace) && Objects.equals(serviceName, that.serviceName) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, incidentId, level, message, stackTrace, serviceName, createdAt);
    }
}
