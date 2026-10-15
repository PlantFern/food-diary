CREATE TABLE specialists
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    user_id     BIGINT                NOT NULL,
    is_active   BIT(1)                NULL,
    is_approved BIT(1)                NULL,
    created_at  datetime              NOT NULL,
    updated_at  datetime              NULL,
    deleted_at  datetime              NULL,
    CONSTRAINT pk_specialists PRIMARY KEY (id)
);

CREATE TABLE user_relation_statuses
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    code VARCHAR(20)           NOT NULL,
    CONSTRAINT pk_user_relation_statuses PRIMARY KEY (id)
);

CREATE TABLE user_relations
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    diary_profile_id   BIGINT                NOT NULL,
    specialist_id      BIGINT                NOT NULL,
    relation_type      VARCHAR(20)           NOT NULL,
    relation_status_id BIGINT                NOT NULL,
    CONSTRAINT pk_user_relations PRIMARY KEY (id)
);

CREATE TABLE user_visibility
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    actor_user_id  BIGINT                NOT NULL,
    target_user_id BIGINT                NOT NULL,
    is_extended    BIT(1)                NOT NULL,
    CONSTRAINT pk_user_visibility PRIMARY KEY (id)
);

ALTER TABLE user_visibility
    ADD CONSTRAINT unique_actor_user_id_target_user_id UNIQUE (actor_user_id, target_user_id);

ALTER TABLE user_relation_statuses
    ADD CONSTRAINT unique_user_relation_statuses_code UNIQUE (code);

ALTER TABLE user_relations
    ADD CONSTRAINT unique_user_relations_diary_profile_id_specialist_id UNIQUE (diary_profile_id, specialist_id);

ALTER TABLE specialists
    ADD CONSTRAINT FK_SPECIALISTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_relations
    ADD CONSTRAINT FK_USER_RELATIONS_ON_RELATION_STATUS FOREIGN KEY (relation_status_id) REFERENCES user_relation_statuses (id);

ALTER TABLE user_relations
    ADD CONSTRAINT FK_USER_RELATIONS_ON_SPECIALIST FOREIGN KEY (specialist_id) REFERENCES specialists (id);

ALTER TABLE user_relations
    ADD CONSTRAINT FK_USER_RELATIONS_ON_DIARY_PROFILE FOREIGN KEY (diary_profile_id) REFERENCES diary_profiles (id);

ALTER TABLE user_visibility
    ADD CONSTRAINT FK_USER_VISIBILITY_ON_ACTOR_USER FOREIGN KEY (actor_user_id) REFERENCES users (id);

ALTER TABLE user_visibility
    ADD CONSTRAINT FK_USER_VISIBILITY_ON_TARGET_USER FOREIGN KEY (target_user_id) REFERENCES users (id);
