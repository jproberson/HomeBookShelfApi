DROP TABLE IF EXISTS "refresh_token";

CREATE TABLE refresh_token
(
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id         UUID         NOT NULL,
    token           VARCHAR(255) NOT NULL,
    expiration_date TIMESTAMP    NOT NULL,
    revoked         BOOLEAN      NOT NULL,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);
