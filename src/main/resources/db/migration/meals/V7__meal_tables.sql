CREATE TABLE meal_types
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    code VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_meal_types PRIMARY KEY (id)
);

CREATE TABLE meal_templates
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    diary_profile_id BIGINT                NOT NULL,
    name             VARCHAR(255)          NULL,
    scheduled_time   time                  NOT NULL,
    frequency        BIGINT                NOT NULL,
    started_at       date                  NOT NULL,
    created_at       datetime              NOT NULL,
    updated_at       datetime              NULL,
    deleted_at       datetime              NULL,
    CONSTRAINT pk_meal_templates PRIMARY KEY (id)
);

CREATE TABLE meal_template_food_records
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    template_id BIGINT                NOT NULL,
    serving_id  BIGINT                NOT NULL,
    amount      FLOAT                 NOT NULL,
    created_at  datetime              NOT NULL,
    updated_at  datetime              NULL,
    CONSTRAINT pk_meal_template_food_records PRIMARY KEY (id)
);

CREATE TABLE meals
(
    id                         BIGINT AUTO_INCREMENT NOT NULL,
    diary_profile_id           BIGINT                NOT NULL,
    meal_type_id               BIGINT                NOT NULL,
    generated_from_template_id BIGINT                NULL,
    photo_path                 VARCHAR(255)          NULL,
    date                       date                  NOT NULL,
    created_at                 datetime              NOT NULL,
    updated_at                 datetime              NULL,
    deleted_at                 datetime              NULL,
    CONSTRAINT pk_meals PRIMARY KEY (id)
);

CREATE TABLE food_records
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    serving_id   BIGINT                NOT NULL,
    meal_data_id BIGINT                NOT NULL,
    amount       FLOAT                 NOT NULL,
    eaten_at     time                  NOT NULL,
    created_at   datetime              NOT NULL,
    CONSTRAINT pk_food_records PRIMARY KEY (id)
);

ALTER TABLE meal_types
    ADD CONSTRAINT uc_meal_types_code UNIQUE (code);

ALTER TABLE meal_templates
    ADD CONSTRAINT FK_MEAL_TEMPLATES_ON_DIARY_PROFILE FOREIGN KEY (diary_profile_id) REFERENCES diary_profiles (id);

ALTER TABLE meal_template_food_records
    ADD CONSTRAINT FK_MEAL_TEMPLATE_FOOD_RECORDS_ON_TEMPLATE FOREIGN KEY (template_id) REFERENCES meal_templates (id);

ALTER TABLE meal_template_food_records
    ADD CONSTRAINT FK_MEAL_TEMPLATE_FOOD_RECORDS_ON_SERVING FOREIGN KEY (serving_id) REFERENCES food_servings (id);

ALTER TABLE meals
    ADD CONSTRAINT FK_MEALS_ON_DIARY_PROFILE FOREIGN KEY (diary_profile_id) REFERENCES diary_profiles (id);

ALTER TABLE meals
    ADD CONSTRAINT FK_MEALS_ON_GENERATED_FROM_TEMPLATE FOREIGN KEY (generated_from_template_id) REFERENCES meal_templates (id);

ALTER TABLE meals
    ADD CONSTRAINT FK_MEALS_ON_MEAL_TYPE FOREIGN KEY (meal_type_id) REFERENCES meal_types (id);

ALTER TABLE food_records
    ADD CONSTRAINT FK_FOOD_RECORDS_ON_MEAL_DATA FOREIGN KEY (meal_data_id) REFERENCES meals (id);

ALTER TABLE food_records
    ADD CONSTRAINT FK_FOOD_RECORDS_ON_SERVING FOREIGN KEY (serving_id) REFERENCES food_servings (id);


-- Canonical meal types used by the diary UI. Keep these IDs stable for seed data.
INSERT INTO meal_types (code) VALUES ('BREAKFAST');
INSERT INTO meal_types (code) VALUES ('LUNCH');
INSERT INTO meal_types (code) VALUES ('SNACK');
INSERT INTO meal_types (code) VALUES ('DINNER');
INSERT INTO meal_types (code) VALUES ('OTHERS');
