-- liquibase formatted sql

-- changeset vbanasevych:1
CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) UNIQUE NOT NULL,
    name          VARCHAR(255)        NOT NULL,
    password_hash VARCHAR(255)
);

CREATE TABLE categories
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    is_expense BOOLEAN      NOT NULL,
    user_id    BIGINT REFERENCES users (id)
);

CREATE TABLE accounts
(
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(100)                 NOT NULL,
    balance  DECIMAL(15, 2)               NOT NULL DEFAULT 0.00,
    currency VARCHAR(10)                  NOT NULL,
    user_id  BIGINT REFERENCES users (id) NOT NULL
);

CREATE TABLE budgets
(
    id           BIGSERIAL PRIMARY KEY,
    limit_amount DECIMAL(15, 2)                    NOT NULL,
    month        DATE                              NOT NULL,
    category_id  BIGINT REFERENCES categories (id) NOT NULL,
    user_id      BIGINT REFERENCES users (id)      NOT NULL
);

CREATE TABLE transactions
(
    id               BIGSERIAL PRIMARY KEY,
    amount           DECIMAL(15, 2)                    NOT NULL,
    date_time        TIMESTAMP                         NOT NULL,
    description      TEXT,
    receipt_file_url VARCHAR(500),
    account_id       BIGINT REFERENCES accounts (id)   NOT NULL,
    category_id      BIGINT REFERENCES categories (id) NOT NULL,
    user_id          BIGINT REFERENCES users (id)      NOT NULL
);