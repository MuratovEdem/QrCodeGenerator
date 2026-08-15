--liquibase formatted sql

--changeset your.name:1
CREATE TABLE audit_logs (
                            id BIGSERIAL PRIMARY KEY,
                            action VARCHAR(255) NOT NULL,
                            target_username VARCHAR(255) NOT NULL,
                            details TEXT,
                            created_by VARCHAR(255),
                            created_at TIMESTAMP
);

--changeset your.name:2
CREATE TABLE ciphers (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255),
                         description TEXT
);

--changeset your.name:3
CREATE TABLE clients (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         inn_kpp VARCHAR(255),
                         created_by VARCHAR(255),
                         created_at TIMESTAMP,
                         updated_by VARCHAR(255),
                         updated_at TIMESTAMP
);

--changeset your.name:4
CREATE TABLE client_files (
                              id BIGSERIAL PRIMARY KEY,
                              file_name VARCHAR(255),
                              file_path VARCHAR(500),
                              content_type VARCHAR(100),
                              client_id BIGINT NOT NULL
);

--changeset your.name:5
CREATE TABLE construction_sites (
                                    id BIGSERIAL PRIMARY KEY,
                                    name VARCHAR(255),
                                    client_id BIGINT NOT NULL
);

--changeset your.name:6
CREATE TABLE contacts (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          post VARCHAR(255),
                          phone_number VARCHAR(50) NOT NULL,
                          email VARCHAR(255),
                          client_id BIGINT NOT NULL
);

--changeset your.name:7
CREATE TABLE contracts (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(255),
                           client_id BIGINT NOT NULL
);

--changeset your.name:8
CREATE TABLE ocr_job (
                         id BIGSERIAL PRIMARY KEY,
                         client_id BIGINT,
                         created_by VARCHAR(255),
                         user_id BIGINT,
                         original_file_path VARCHAR(500),
                         status VARCHAR(50),
                         error_message TEXT,
                         created_at TIMESTAMP,
                         finished_at TIMESTAMP
);

--changeset your.name:9
CREATE TABLE ocr_protocol_preview (
                                      id BIGSERIAL PRIMARY KEY,
                                      ocr_job_id BIGINT,
                                      file_name VARCHAR(255),
                                      protocol_number VARCHAR(255),
                                      issue_date VARCHAR(255)
);

--changeset your.name:10
CREATE TABLE protocols (
                           id BIGSERIAL PRIMARY KEY,
                           protocol_number VARCHAR(255) NOT NULL,
                           issue_date DATE NOT NULL,
                           file_path VARCHAR(500),
                           status VARCHAR(50),
                           client_id BIGINT NOT NULL,
                           created_by VARCHAR(255),
                           created_at TIMESTAMP,
                           updated_by VARCHAR(255),
                           updated_at TIMESTAMP,
                           CONSTRAINT uq_protocol_number UNIQUE (protocol_number)
);

--changeset your.name:11
CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL UNIQUE
);

--changeset your.name:12
CREATE TABLE unique_numbers (
                                id BIGSERIAL PRIMARY KEY,
                                number BIGINT UNIQUE,
                                client_id BIGINT NOT NULL
);

--changeset your.name:13
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(255),
                       surname VARCHAR(255),
                       patronymic VARCHAR(255),
                       password_temporary BOOLEAN DEFAULT FALSE,
                       role_id BIGINT,
                       enabled BOOLEAN DEFAULT TRUE
);

--changeset your.name:14
CREATE TABLE failed_files (
                              id BIGSERIAL PRIMARY KEY,
                              file_name VARCHAR(255) NOT NULL,
                              file_path VARCHAR(500) NOT NULL,
                              content_type VARCHAR(100),
                              client_id BIGINT NOT NULL
);