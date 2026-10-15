CREATE TABLE v_meal_day_records
(
    record_id                  BIGINT       NOT NULL,
    meal_id                    BIGINT       NULL,
    serving_id                 BIGINT       NULL,
    amount                     FLOAT        NULL,
    eaten_at                   time         NULL,
    diary_profile_id           BIGINT       NULL,
    meal_type_id               BIGINT       NULL,
    meal_date                  date         NULL,
    generated_from_template_id BIGINT       NULL,
    photo_path                 VARCHAR(255) NULL,
    meal_type_code             VARCHAR(255) NULL,
    CONSTRAINT pk_v_meal_day_records PRIMARY KEY (record_id)
);

CREATE TABLE v_food_servings
(
    serving_id           BIGINT       NOT NULL,
    food_serving_type    VARCHAR(255) NULL,
    food_serving_item_id BIGINT       NULL,
    item_name            VARCHAR(255) NULL,
    item_photo_path      VARCHAR(255) NULL,
    serving_amount       FLOAT        NULL,
    gram_weight          FLOAT        NULL,
    serving_unit_id      BIGINT       NULL,
    serving_unit_code    VARCHAR(255) NULL,
    CONSTRAINT pk_v_food_servings PRIMARY KEY (serving_id)
);

CREATE TABLE v_product_nutrients
(
    product_id     BIGINT       NOT NULL,
    nutrient_id    BIGINT       NULL,
    product_code   VARCHAR(255) NULL,
    nutrient_code  VARCHAR(255) NULL,
    amount_per100g FLOAT        NULL,
    unit_code      VARCHAR(255) NULL,
    CONSTRAINT pk_v_product_nutrients PRIMARY KEY (product_id)
);

CREATE TABLE v_item_nutrients
(
    item_id        BIGINT       NOT NULL,
    item_type      VARCHAR(255) NULL,
    nutrient_id    BIGINT       NULL,
    nutrient_code  VARCHAR(255) NULL,
    amount_per100g FLOAT        NULL,
    unit_code      VARCHAR(255) NULL,
    CONSTRAINT pk_v_item_nutrients PRIMARY KEY (item_id)
);

CREATE TABLE v_products_basic
(
    product_id              BIGINT       NOT NULL,
    product_code            VARCHAR(255) NULL,
    product_description     VARCHAR(255) NULL,
    photo_path              VARCHAR(255) NULL,
    is_public               BIT(1)       NULL,
    created_by_id           BIGINT       NULL,
    category_id             BIGINT       NULL,
    category_code           VARCHAR(255) NULL,
    entity_status_id        BIGINT       NULL,
    entity_status_code      VARCHAR(255) NULL,
    data_source_id          BIGINT       NULL,
    data_source_code        VARCHAR(255) NULL,
    base_serving_id         BIGINT       NULL,
    serving_amount          BIGINT       NULL,
    gram_weight_per_serving FLOAT        NULL,
    serving_unit_code       VARCHAR(255) NULL,
    CONSTRAINT pk_v_products_basic PRIMARY KEY (product_id)
);

CREATE TABLE v_recipe_nutrients
(
    recipe_id      BIGINT       NOT NULL,
    nutrient_id    BIGINT       NULL,
    recipe_code    VARCHAR(255) NULL,
    nutrient_code  VARCHAR(255) NULL,
    amount_per100g FLOAT        NULL,
    unit_code      VARCHAR(255) NULL,
    CONSTRAINT pk_v_recipe_nutrients PRIMARY KEY (recipe_id)
);

CREATE TABLE v_recipes_basic
(
    recipe_id          BIGINT       NOT NULL,
    recipe_name        VARCHAR(255) NULL,
    recipe_description VARCHAR(255) NULL,
    recipe             VARCHAR(255) NULL,
    photo_path         VARCHAR(255) NULL,
    total_weight_grams FLOAT        NULL,
    is_public          BIT(1)       NULL,
    created_by_id      BIGINT       NULL,
    entity_status_id   BIGINT       NULL,
    entity_status_code VARCHAR(255) NULL,
    CONSTRAINT pk_v_recipes_basic PRIMARY KEY (recipe_id)
);