--liquibase formatted sql

--changeset your.name:21
INSERT INTO roles (name) VALUES
                             ('ADMIN'),
                             ('USER');

-- Вставка пользователя admin с паролем "admin" (bcrypt)
INSERT INTO users (username, password, name, surname, patronymic, password_temporary, role_id, enabled)
VALUES (
           'admin',
           '$2a$12$msZtWTDC2mFh4D61J.3SsePFUMTPyCBr.NBfLkOkwCgNMJNhFNDEq',
           'Admin',
           'Admin',
           NULL,
           true,
           (SELECT id FROM roles WHERE name = 'ADMIN'),
           true
       );