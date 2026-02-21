CREATE TABLE incident_log (
      id BIGSERIAL PRIMARY KEY,
      incident_id BIGINT NOT NULL,
      level VARCHAR(10) NOT NULL, -- INFO, WARN, ERROR
      message VARCHAR(255) NOT NULL,
      stack_trace TEXT, -- CLOB / LONG TEXT
      service_name VARCHAR(100) NOT NULL,
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT fk_log_incident
          FOREIGN KEY (incident_id)
              REFERENCES incident(id)
              ON DELETE CASCADE
);

CREATE INDEX idx_log_incident ON incident_log(incident_id);