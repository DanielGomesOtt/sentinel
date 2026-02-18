CREATE TABLE organization (
   id              BIGSERIAL PRIMARY KEY,
   name            VARCHAR(150) NOT NULL,
   status          smallint NOT NULL,
   created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);