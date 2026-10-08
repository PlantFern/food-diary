ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE user_roles
    ADD CONSTRAINT FK_USER_ROLES_ON_ROLE FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE user_roles
    ADD CONSTRAINT FK_USER_ROLES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_login UNIQUE (login);

CREATE TABLE users
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    email         VARCHAR(255)          NOT NULL,
    login         VARCHAR(30)           NULL,
    hash_password VARCHAR(60)           NOT NULL,
    created_at    datetime              NOT NULL,
    updated_at    datetime              NULL,
    deleted_at    datetime              NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    id      BIGINT AUTO_INCREMENT NOT NULL,
    user_id BIGINT                NOT NULL,
    role_id BIGINT                NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    code VARCHAR(32)           NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_code UNIQUE (code);