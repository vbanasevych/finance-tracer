-- liquibase formatted sql

-- changeset vbanasevych:2
INSERT INTO users (id, email, name, password_hash)
VALUES (1, 'test@example.com', 'Test User', 'nohashyet');

ALTER SEQUENCE users_id_seq RESTART WITH 2;

INSERT INTO categories (id, name, is_expense, user_id) VALUES (1, 'Продукти', true, 1);
INSERT INTO categories (id, name, is_expense, user_id) VALUES (2, 'Комуналка', true, 1);
INSERT INTO categories (id, name, is_expense, user_id) VALUES (3, 'Зарплата', false, 1);
ALTER SEQUENCE categories_id_seq RESTART WITH 4;

INSERT INTO accounts (id, name, balance, currency, user_id) VALUES (1, 'Картка Моно', 15000.00, 'UAH', 1);
INSERT INTO accounts (id, name, balance, currency, user_id) VALUES (2, 'Готівка', 2000.00, 'UAH', 1);
ALTER SEQUENCE accounts_id_seq RESTART WITH 3;