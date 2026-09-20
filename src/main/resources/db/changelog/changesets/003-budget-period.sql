-- liquibase formatted sql

-- changeset vbanasevych:3
ALTER TABLE budgets DROP COLUMN month;
ALTER TABLE budgets ADD COLUMN start_date DATE NOT NULL DEFAULT CURRENT_DATE;
ALTER TABLE budgets ADD COLUMN end_date DATE NOT NULL DEFAULT CURRENT_DATE;