CREATE TABLE forgot_password_token (
   id              BIGSERIAL PRIMARY KEY,
   user_id         BIGSERIAL REFERENCES users(id),
   code            VARCHAR(150),
   expires_at      TIMESTAMP NOT NULL,
   used            BOOLEAN,
   created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);