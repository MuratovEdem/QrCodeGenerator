--liquibase formatted sql

--changeset your.name:14
ALTER TABLE client_files
    ADD CONSTRAINT fk_client_files_client
        FOREIGN KEY (client_id) REFERENCES clients(id);

--changeset your.name:15
ALTER TABLE construction_sites
    ADD CONSTRAINT fk_construction_sites_client
        FOREIGN KEY (client_id) REFERENCES clients(id);

--changeset your.name:16
ALTER TABLE contacts
    ADD CONSTRAINT fk_contacts_client
        FOREIGN KEY (client_id) REFERENCES clients(id);

--changeset your.name:17
ALTER TABLE contracts
    ADD CONSTRAINT fk_contracts_client
        FOREIGN KEY (client_id) REFERENCES clients(id);

--changeset your.name:18
ALTER TABLE protocols
    ADD CONSTRAINT fk_protocols_client
        FOREIGN KEY (client_id) REFERENCES clients(id);

--changeset your.name:19
ALTER TABLE unique_numbers
    ADD CONSTRAINT fk_unique_numbers_client
        FOREIGN KEY (client_id) REFERENCES clients(id);

--changeset your.name:20
ALTER TABLE users
    ADD CONSTRAINT fk_users_role
        FOREIGN KEY (role_id) REFERENCES roles(id);