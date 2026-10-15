ALTER TABLE weight_logs
    ADD CONSTRAINT FK_WEIGHT_LOGS_ON_DIARY_PROFILE FOREIGN KEY (diary_profile_id) REFERENCES diary_profiles (id);

CREATE TABLE diary_comments
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    diary_profile_id BIGINT                NOT NULL,
    commentable_type VARCHAR(255)          NOT NULL,
    commentable_id   BIGINT                NOT NULL,
    body             VARCHAR(2000)         NOT NULL,
    created_by       BIGINT                NOT NULL,
    created_at       datetime              NOT NULL,
    updated_at       datetime              NULL,
    deleted_at       datetime              NULL,
    CONSTRAINT pk_diary_comments PRIMARY KEY (id)
);

CREATE TABLE diary_profiles
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    user_id    BIGINT                NOT NULL,
    height     FLOAT                 NULL,
    birth_date date                  NULL,
    created_at datetime              NOT NULL,
    updated_at datetime              NULL,
    deleted_at datetime              NULL,
    gender_id  BIGINT                NULL,
    CONSTRAINT pk_diary_profiles PRIMARY KEY (id)
);

CREATE TABLE genders
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    code VARCHAR(30)           NOT NULL,
    CONSTRAINT pk_genders PRIMARY KEY (id)
);

CREATE TABLE goals
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    diary_profile_id BIGINT                NOT NULL,
    planned_weight   FLOAT                 NULL,
    start_date       date                  NOT NULL,
    actual_end_date  date                  NULL,
    planned_end_date date                  NULL,
    created_by       BIGINT                NOT NULL,
    created_at       datetime              NOT NULL,
    updated_at       datetime              NULL,
    deleted_at       datetime              NULL,
    CONSTRAINT pk_goals PRIMARY KEY (id)
);

CREATE TABLE goal_nutrients
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    goal_id     BIGINT                NOT NULL,
    nutrient_id BIGINT                NOT NULL,
    amount      FLOAT                 NOT NULL,
    CONSTRAINT pk_goal_nutrients PRIMARY KEY (id)
);

CREATE TABLE sleep_logs
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    diary_profile_id BIGINT                NOT NULL,
    began_at         datetime              NOT NULL,
    ended_at         datetime              NOT NULL,
    created_at       datetime              NOT NULL,
    CONSTRAINT pk_sleep_logs PRIMARY KEY (id)
);

CREATE TABLE weight_logs
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    diary_profile_id BIGINT                NOT NULL,
    weight           FLOAT                 NOT NULL,
    created_at       datetime              NOT NULL,
    CONSTRAINT pk_weight_logs PRIMARY KEY (id)
);

ALTER TABLE genders
    ADD CONSTRAINT uc_6794e03220730f26afe4a4b3e UNIQUE (code);

ALTER TABLE profile_hidden_nutrients
    ADD CONSTRAINT uc_4856ab80209fc578ef2a34397 UNIQUE (profile_feature_setting_id, nutrient_id);

ALTER TABLE goal_nutrients
    ADD CONSTRAINT uc_6918b2e4f0ec0855cfc0f4a29 UNIQUE (goal_id, nutrient_id);

ALTER TABLE diary_comments
    ADD CONSTRAINT FK_DIARY_COMMENTS_ON_DIARY_PROFILE FOREIGN KEY (diary_profile_id) REFERENCES diary_profiles (id);

ALTER TABLE diary_profiles
    ADD CONSTRAINT FK_DIARY_PROFILES_ON_GENDER FOREIGN KEY (gender_id) REFERENCES genders (id);

ALTER TABLE goals
    ADD CONSTRAINT FK_GOALS_ON_DIARY_PROFILE FOREIGN KEY (diary_profile_id) REFERENCES diary_profiles (id);

ALTER TABLE goal_nutrients
    ADD CONSTRAINT FK_GOAL_NUTRIENTS_ON_GOAL FOREIGN KEY (goal_id) REFERENCES goals (id);

ALTER TABLE profile_feature_settings
    ADD CONSTRAINT FK_PROFILE_FEATURE_SETTINGS_ON_DIARY_PROFILE FOREIGN KEY (diary_profile_id) REFERENCES diary_profiles (id);

ALTER TABLE profile_hidden_nutrients
    ADD CONSTRAINT FK_PROFILE_HIDDEN_NUTRIENTS_ON_PROFILE_FEATURE_SETTING FOREIGN KEY (profile_feature_setting_id) REFERENCES profile_feature_settings (id);

CREATE TABLE profile_feature_settings
(
    id                     BIGINT AUTO_INCREMENT NOT NULL,
    diary_profile_id       BIGINT                NOT NULL,
    show_sleep             BIT(1)                NOT NULL,
    show_sleep_logs        BIT(1)                NOT NULL,
    show_weight            BIT(1)                NOT NULL,
    show_weight_logs       BIT(1)                NOT NULL,
    show_allergens_warning BIT(1)                NOT NULL,
    created_by             BIGINT                NOT NULL,
    created_at             datetime              NOT NULL,
    updated_at             datetime              NULL,
    expired_at             datetime              NULL,
    CONSTRAINT pk_profile_feature_settings PRIMARY KEY (id)
);

CREATE TABLE profile_hidden_nutrients
(
    id                         BIGINT AUTO_INCREMENT NOT NULL,
    profile_feature_setting_id BIGINT                NOT NULL,
    nutrient_id                BIGINT                NOT NULL,
    CONSTRAINT pk_profile_hidden_nutrients PRIMARY KEY (id)
);

ALTER TABLE sleep_logs
    ADD CONSTRAINT FK_SLEEP_LOGS_ON_DIARY_PROFILE FOREIGN KEY (diary_profile_id) REFERENCES diary_profiles (id);