CREATE TABLE system_integration (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    client_id VARCHAR(100) NOT NULL UNIQUE,
    client_secret_hash VARCHAR(255) NOT NULL,
    organization_id BIGINT NOT NULL,
    created_by BIGINT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    last_used_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,

    CONSTRAINT fk_system_integration_organization
    FOREIGN KEY (organization_id)
        REFERENCES organization(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_system_integration_user
    FOREIGN KEY (created_by)
        REFERENCES users(id)
        ON DELETE SET NULL
);