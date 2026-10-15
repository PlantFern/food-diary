CREATE TABLE recipes
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    code               VARCHAR(40)           NOT NULL,
    name               VARCHAR(100)          NOT NULL,
    `description`      VARCHAR(255)          NOT NULL,
    recipe             VARCHAR(255)          NOT NULL,
    photo_path         VARCHAR(255)          NOT NULL,
    total_weight_grams FLOAT                 NULL,
    is_public          BIT(1)                NOT NULL,
    entity_status_id   BIGINT                NOT NULL,
    created_by         BIGINT                NULL,
    created_at         datetime              NOT NULL,
    updated_at         datetime              NULL,
    deleted_at         datetime              NULL,
    CONSTRAINT pk_recipes PRIMARY KEY (id)
);

CREATE TABLE recipe_components
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    recipe_id          BIGINT                NOT NULL,
    product_serving_id BIGINT                NOT NULL,
    amount             FLOAT                 NOT NULL,
    CONSTRAINT pk_recipe_components PRIMARY KEY (id)
);

ALTER TABLE recipes
    ADD CONSTRAINT uc_recipes_code UNIQUE (code);

ALTER TABLE recipes
    ADD CONSTRAINT uc_recipes_description UNIQUE (`description`);

ALTER TABLE recipes
    ADD CONSTRAINT uc_recipes_name UNIQUE (name);

ALTER TABLE recipes
    ADD CONSTRAINT uc_recipes_photo_path UNIQUE (photo_path);

ALTER TABLE recipes
    ADD CONSTRAINT uc_recipes_recipe UNIQUE (recipe);

ALTER TABLE recipes
    ADD CONSTRAINT FK_RECIPES_ON_ENTITY_STATUS FOREIGN KEY (entity_status_id) REFERENCES entity_statuses (id);

ALTER TABLE recipe_components
    ADD CONSTRAINT FK_RECIPE_COMPONENTS_ON_PRODUCT_SERVING FOREIGN KEY (product_serving_id) REFERENCES food_servings (id);

ALTER TABLE recipe_components
    ADD CONSTRAINT FK_RECIPE_COMPONENTS_ON_RECIPE FOREIGN KEY (recipe_id) REFERENCES recipes (id);