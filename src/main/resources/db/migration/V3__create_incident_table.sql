CREATE TABLE incident (
      id BIGSERIAL PRIMARY KEY,
      title VARCHAR(200) NOT NULL,
      description TEXT NOT NULL,
      severity VARCHAR(20) NOT NULL, -- LOW, MEDIUM, HIGH, CRITICAL
      status VARCHAR(20) NOT NULL, -- OPEN, UNDER_REVIEW, IN_CORRECTION, RESOLVED, CLOSED
      service_name VARCHAR(100) NOT NULL,
      sla_deadline TIMESTAMP NOT NULL,
      sla_violated BOOLEAN NOT NULL DEFAULT FALSE,
      created_by BIGINT NOT NULL,
      organization_id BIGINT NOT NULL,
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT fk_incident_user FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE INDEX idx_incident_status ON incident(status);
CREATE INDEX idx_incident_severity ON incident(severity);
CREATE INDEX idx_incident_service ON incident(service_name);