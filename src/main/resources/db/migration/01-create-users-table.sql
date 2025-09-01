CREATE SCHEMA IF NOT EXISTS bank;

CREATE TABLE IF NOT EXISTS bank.users (
    id              bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    login           varchar(64) NOT NULL UNIQUE,
    password        varchar(64) NOT NULL,
    role            varchar(16) NOT NULL,
    CONSTRAINT chk_role CHECK (role IN ('ADMIN', 'USER'))
);