CREATE TABLE categories
(
    id        BIGINT AUTO_INCREMENT NOT NULL,
    code      VARCHAR(255)          NOT NULL,
    parent_id BIGINT                NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE entity_statuses
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    code VARCHAR(40)           NOT NULL,
    CONSTRAINT pk_entity_statuses PRIMARY KEY (id)
);

CREATE TABLE data_sources
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    code VARCHAR(45)           NOT NULL,
    CONSTRAINT pk_data_sources PRIMARY KEY (id)
);

CREATE TABLE nutrient_units
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    code VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_nutrient_units PRIMARY KEY (id)
);

CREATE TABLE nutrients
(
    id      BIGINT AUTO_INCREMENT NOT NULL,
    code    VARCHAR(255)          NOT NULL,
    unit_id BIGINT                NOT NULL,
    CONSTRAINT pk_nutrients PRIMARY KEY (id)
);

CREATE TABLE serving_units
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    code VARCHAR(40)           NOT NULL,
    CONSTRAINT pk_serving_units PRIMARY KEY (id)
);

ALTER TABLE serving_units
    ADD CONSTRAINT uc_serving_units_code UNIQUE (code);

ALTER TABLE data_sources
    ADD CONSTRAINT uc_data_sources_code UNIQUE (code);

ALTER TABLE entity_statuses
    ADD CONSTRAINT uc_entity_statuses_code UNIQUE (code);

ALTER TABLE categories
    ADD CONSTRAINT uc_categories_code UNIQUE (code);

ALTER TABLE nutrient_units
    ADD CONSTRAINT uc_nutrient_units_code UNIQUE (code);

ALTER TABLE nutrients
    ADD CONSTRAINT FK_NUTRIENTS_ON_UNIT FOREIGN KEY (unit_id) REFERENCES nutrient_units (id);

ALTER TABLE categories
    ADD CONSTRAINT FK_CATEGORIES_ON_PARENT FOREIGN KEY (parent_id) REFERENCES categories (id);

-- Canonical nutrient units and nutrients; IDs are relied on by the catalog seed.
INSERT INTO nutrient_units (code) VALUES ('kcal');
INSERT INTO nutrient_units (code) VALUES ('g');

INSERT INTO nutrients (code, unit_id)
SELECT 'ENERGY', id FROM nutrient_units WHERE code = 'kcal' LIMIT 1;
INSERT INTO nutrients (code, unit_id)
SELECT 'PROTEIN', id FROM nutrient_units WHERE code = 'g' LIMIT 1;
INSERT INTO nutrients (code, unit_id)
SELECT 'FAT', id FROM nutrient_units WHERE code = 'g' LIMIT 1;
INSERT INTO nutrients (code, unit_id)
SELECT 'CARBS', id FROM nutrient_units WHERE code = 'g' LIMIT 1;

INSERT INTO serving_units (code) VALUES ('g');
INSERT INTO serving_units (code) VALUES ('ml');
INSERT INTO serving_units (code) VALUES ('pcs');

INSERT INTO entity_statuses (code) VALUES ('ACTIVE');
INSERT INTO entity_statuses (code) VALUES ('DRAFT');
INSERT INTO entity_statuses (code) VALUES ('ARCHIVED');

INSERT INTO data_sources (code) VALUES ('USER');
INSERT INTO data_sources (code) VALUES ('SYSTEM');
INSERT INTO data_sources (code) VALUES ('USDA');
INSERT INTO data_sources (code) VALUES ('BARCODE');
INSERT INTO data_sources (code) VALUES ('CUSTOM');
INSERT INTO data_sources (code) VALUES ('NUTRIENT_RECORDING');
