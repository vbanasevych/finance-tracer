-- liquibase formatted sql
-- changeset vbanasevych:5

ALTER TABLE users ADD COLUMN telegram_chat_id BIGINT UNIQUE;
ALTER TABLE users ADD COLUMN telegram_verification_token VARCHAR(255);