CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(150) NOT NULL,
    email           VARCHAR(150) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    organization_id BIGSERIAL REFERENCES organization(id),
    role            VARCHAR(20) NOT NULL, -- USER, TECH, ADMIN
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);