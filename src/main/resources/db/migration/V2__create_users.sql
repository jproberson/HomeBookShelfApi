DROP TABLE IF EXISTS "users";
CREATE TABLE "users"
(
    id         UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    username   VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    role       VARCHAR(255) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--default password is password
INSERT INTO users (id, username, password, role, created_at, updated_at)
VALUES ('00000000-0000-0000-0000-000000000001', 'admin',
        '$2a$10$esTztHt/FfXQ9srLcBJf9etQaYTZ.N0gH6ONxmlRCk6KmeF5AMGnW',
        'ADMIN', NOW(), NOW());