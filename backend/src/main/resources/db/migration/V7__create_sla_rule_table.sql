CREATE TABLE sla_rule (
      severity VARCHAR(20) PRIMARY KEY,
      duration_hours INTEGER NOT NULL
);

INSERT INTO sla_rule (severity, duration_hours) VALUES ('LOW', 72),
                                                    ('MEDIUM', 48),
                                                    ('HIGH', 24),
                                                    ('CRITICAL', 4);