-- liquibase formatted sql
-- changeset vbanasevych:5

ALTER TABLE users ADD COLUMN role VARCHAR(50) DEFAULT 'ROLE_USER';

INSERT INTO users (id, email, name, password_hash, role)
VALUES (2, 'vbanasevych@knu.ua', 'Vika Admin', '$2a$10$hvfSFh8EXDOOFmyzv/85jOcEqf81JebB8mgSLHAZ5WUpMN5839bFO', 'ROLE_ADMIN');
