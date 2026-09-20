-- liquibase formatted sql

-- changeset vbanasevych:4
ALTER TABLE accounts ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE categories ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;