CREATE TABLE incident_history (
      id BIGSERIAL PRIMARY KEY,
      incident_id BIGINT NOT NULL,
      previous_status VARCHAR(20),
      new_status VARCHAR(20) NOT NULL,
      action VARCHAR(100) NOT NULL,
      performed_by BIGINT NOT NULL,
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT fk_history_incident
          FOREIGN KEY (incident_id)
              REFERENCES incident(id)
              ON DELETE CASCADE,

      CONSTRAINT fk_history_user
          FOREIGN KEY (performed_by)
              REFERENCES users(id)
);

CREATE INDEX idx_history_incident ON incident_history(incident_id);