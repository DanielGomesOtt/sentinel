CREATE TABLE incident_history (
      id BIGSERIAL PRIMARY KEY,
      incident_id BIGINT NOT NULL,
      previous_status VARCHAR(20),
      new_status VARCHAR(20) NOT NULL,
      action VARCHAR(100) NOT NULL,
      performed_by BIGINT,
      performed_by_system_integration BIGINT,
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT fk_history_incident
          FOREIGN KEY (incident_id)
              REFERENCES incident(id)
              ON DELETE CASCADE,

      CONSTRAINT fk_history_user
          FOREIGN KEY (performed_by)
              REFERENCES users(id),

      CONSTRAINT fk_history_system_integration
          FOREIGN KEY (performed_by_system_integration)
              REFERENCES system_integration(id)
);

CREATE INDEX idx_history_incident ON incident_history(incident_id);