CREATE TABLE organization (
   id              BIGSERIAL PRIMARY KEY,
   name            VARCHAR(150) NOT NULL,
   status           VARCHAR(150) NOT NULL UNIQUE,
   created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);