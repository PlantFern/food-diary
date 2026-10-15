CREATE TABLE products
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    code             VARCHAR(40)           NOT NULL,
    `description`    VARCHAR(150)          NULL,
    category_id      BIGINT                NULL,
    photo_path       VARCHAR(255)          NULL,
    is_public        BIT(1)                NOT NULL,
    entity_status_id BIGINT                NOT NULL,
    created_by       BIGINT                NULL,
    created_at       datetime              NOT NULL,
    updated_at       datetime              NULL,
    deleted_at       datetime              NULL,
    CONSTRAINT pk_products PRIMARY KEY (id)
);

CREATE TABLE food_servings
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    item_type       VARCHAR(255)          NOT NULL,
    item_id         BIGINT                NOT NULL,
    amount          FLOAT                 NOT NULL,
    gram_weight     FLOAT                 NOT NULL,
    serving_unit_id BIGINT                NOT NULL,
    `description`   VARCHAR(255)          NULL,
    created_at      datetime              NOT NULL,
    updated_at      datetime              NULL,
    CONSTRAINT pk_food_servings PRIMARY KEY (id)
);

CREATE TABLE branded_products
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    product_id      BIGINT                NOT NULL,
    barcode         VARCHAR(100)          NOT NULL,
    base_serving_id BIGINT                NOT NULL,
    created_at      datetime              NOT NULL,
    updated_at      datetime              NULL,
    deleted_at      datetime              NULL,
    CONSTRAINT pk_branded_products PRIMARY KEY (id)
);

CREATE TABLE product_data_sources
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    product_id     BIGINT                NOT NULL,
    data_source_id BIGINT                NOT NULL,
    external_id    VARCHAR(45)           NOT NULL,
    CONSTRAINT pk_product_data_sources PRIMARY KEY (id)
);

CREATE TABLE product_nutrients
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    product_id  BIGINT                NOT NULL,
    nutrient_id BIGINT                NOT NULL,
    amount      FLOAT                 NOT NULL,
    CONSTRAINT pk_product_nutrients PRIMARY KEY (id)
);

CREATE TABLE product_requests
(
    id                             BIGINT AUTO_INCREMENT NOT NULL,
    product_id                     BIGINT                NOT NULL,
    front_photo_path               VARCHAR(200)          NOT NULL,
    product_composition_photo_path VARCHAR(200)          NOT NULL,
    product_nutrition_photo_path   VARCHAR(200)          NOT NULL,
    barcode_photo_path             VARCHAR(200)          NOT NULL,
    created_by                     BIGINT                NOT NULL,
    created_at                     datetime              NOT NULL,
    updated_at                     datetime              NULL,
    deleted_at                     datetime              NULL,
    CONSTRAINT pk_product_requests PRIMARY KEY (id)
);

ALTER TABLE products
    ADD CONSTRAINT uc_products_code UNIQUE (code);

ALTER TABLE product_nutrients
    ADD CONSTRAINT unique_product_id_nutrient_id UNIQUE (product_id, nutrient_id);

ALTER TABLE branded_products
    ADD CONSTRAINT uc_branded_products_barcode UNIQUE (barcode);

ALTER TABLE branded_products
    ADD CONSTRAINT uc_branded_products_product UNIQUE (product_id);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_ENTITY_STATUS FOREIGN KEY (entity_status_id) REFERENCES entity_statuses (id);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE product_nutrients
    ADD CONSTRAINT FK_PRODUCT_NUTRIENTS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE product_nutrients
    ADD CONSTRAINT FK_PRODUCT_NUTRIENTS_ON_NUTRIENT FOREIGN KEY (nutrient_id) REFERENCES nutrients (id);

ALTER TABLE product_requests
    ADD CONSTRAINT FK_PRODUCT_REQUESTS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE product_data_sources
    ADD CONSTRAINT FK_PRODUCT_DATA_SOURCES_ON_DATA_SOURCE FOREIGN KEY (data_source_id) REFERENCES data_sources (id);

ALTER TABLE product_data_sources
    ADD CONSTRAINT FK_PRODUCT_DATA_SOURCES_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE food_servings
    ADD CONSTRAINT FK_FOOD_SERVINGS_ON_SERVING_UNIT FOREIGN KEY (serving_unit_id) REFERENCES serving_units (id);

ALTER TABLE branded_products
    ADD CONSTRAINT FK_BRANDED_PRODUCTS_ON_BASE_SERVING FOREIGN KEY (base_serving_id) REFERENCES food_servings (id);

ALTER TABLE branded_products
    ADD CONSTRAINT FK_BRANDED_PRODUCTS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);
