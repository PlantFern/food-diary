CREATE OR REPLACE VIEW v_meal_day_records AS
SELECT
    fr.id              AS record_id,
    fr.meal_data_id    AS meal_id,
    fr.serving_id      AS serving_id,
    fr.amount          AS amount,
    fr.eaten_at        AS eaten_at,
    m.diary_profile_id AS diary_profile_id,
    m.meal_type_id     AS meal_type_id,
    m.date             AS meal_date,
    m.generated_from_template_id AS generated_from_template_id,
    m.photo_path       AS photo_path,
    mt.code            AS meal_type_code
FROM food_records fr
JOIN meals m ON m.id = fr.meal_data_id AND m.deleted_at IS NULL
JOIN meal_types mt ON mt.id = m.meal_type_id;

CREATE OR REPLACE VIEW v_food_servings AS
SELECT
    fs.id              AS serving_id,
    fs.item_type       AS food_serving_type,
    fs.item_id         AS food_serving_item_id,
    CASE
        WHEN fs.item_type = 'PRODUCT' THEN p.code
        WHEN fs.item_type = 'RECIPE' THEN r.name
        ELSE NULL
    END                AS item_name,
    CASE
        WHEN fs.item_type = 'PRODUCT' THEN p.photo_path
        WHEN fs.item_type = 'RECIPE' THEN r.photo_path
        ELSE NULL
    END                AS item_photo_path,
    fs.amount          AS serving_amount,
    fs.gram_weight     AS gram_weight,
    fs.serving_unit_id AS serving_unit_id,
    su.code            AS serving_unit_code
FROM food_servings fs
LEFT JOIN products p ON fs.item_type = 'PRODUCT' AND p.id = fs.item_id AND p.deleted_at IS NULL
LEFT JOIN recipes r ON fs.item_type = 'RECIPE' AND r.id = fs.item_id AND r.deleted_at IS NULL
LEFT JOIN serving_units su ON su.id = fs.serving_unit_id;

CREATE OR REPLACE VIEW v_product_nutrients AS
SELECT
    pn.product_id  AS product_id,
    pn.nutrient_id AS nutrient_id,
    p.code         AS product_code,
    n.code         AS nutrient_code,
    pn.amount      AS amount_per100g,
    nu.code        AS unit_code
FROM product_nutrients pn
JOIN products p ON p.id = pn.product_id AND p.deleted_at IS NULL
JOIN nutrients n ON n.id = pn.nutrient_id
JOIN nutrient_units nu ON nu.id = n.unit_id;

CREATE OR REPLACE VIEW v_item_nutrients AS
SELECT
    pn.product_id  AS item_id,
    'PRODUCT'      AS item_type,
    pn.nutrient_id AS nutrient_id,
    n.code         AS nutrient_code,
    pn.amount      AS amount_per100g,
    nu.code        AS unit_code
FROM product_nutrients pn
JOIN nutrients n ON n.id = pn.nutrient_id
JOIN nutrient_units nu ON nu.id = n.unit_id
UNION ALL
SELECT
    rc.recipe_id   AS item_id,
    'RECIPE'       AS item_type,
    pn.nutrient_id AS nutrient_id,
    n.code         AS nutrient_code,
    SUM(pn.amount * rc.amount / 100.0) AS amount_per100g,
    nu.code        AS unit_code
FROM recipe_components rc
JOIN product_nutrients pn ON pn.product_id = rc.product_id
JOIN nutrients n ON n.id = pn.nutrient_id
JOIN nutrient_units nu ON nu.id = n.unit_id
GROUP BY rc.recipe_id, pn.nutrient_id, n.code, nu.code;

CREATE OR REPLACE VIEW v_products_basic AS
SELECT
    p.id               AS product_id,
    p.code             AS product_code,
    p.description      AS product_description,
    p.photo_path       AS photo_path,
    p.is_public        AS is_public,
    p.created_by       AS created_by_id,
    p.category_id      AS category_id,
    c.code             AS category_code,
    p.entity_status_id AS entity_status_id,
    es.code            AS entity_status_code,
    pds.data_source_id AS data_source_id,
    ds.code            AS data_source_code,
    bp.base_serving_id AS base_serving_id,
    fs.amount          AS serving_amount,
    fs.gram_weight     AS gram_weight_per_serving,
    su.code            AS serving_unit_code
FROM products p
LEFT JOIN categories c ON c.id = p.category_id
LEFT JOIN entity_statuses es ON es.id = p.entity_status_id
LEFT JOIN product_data_sources pds ON pds.product_id = p.id
LEFT JOIN data_sources ds ON ds.id = pds.data_source_id
LEFT JOIN branded_products bp ON bp.product_id = p.id AND bp.deleted_at IS NULL
LEFT JOIN food_servings fs ON fs.id = bp.base_serving_id
LEFT JOIN serving_units su ON su.id = fs.serving_unit_id
WHERE p.deleted_at IS NULL;

CREATE OR REPLACE VIEW v_recipe_nutrients AS
SELECT
    rc.recipe_id   AS recipe_id,
    pn.nutrient_id AS nutrient_id,
    r.name         AS recipe_code,
    n.code         AS nutrient_code,
    SUM(pn.amount * rc.amount / 100.0) AS amount_per100g,
    nu.code        AS unit_code
FROM recipe_components rc
JOIN recipes r ON r.id = rc.recipe_id AND r.deleted_at IS NULL
JOIN product_nutrients pn ON pn.product_id = rc.product_id
JOIN nutrients n ON n.id = pn.nutrient_id
JOIN nutrient_units nu ON nu.id = n.unit_id
GROUP BY rc.recipe_id, pn.nutrient_id, r.name, n.code, nu.code;

CREATE OR REPLACE VIEW v_recipes_basic AS
SELECT
    r.id               AS recipe_id,
    r.name             AS recipe_name,
    r.description      AS recipe_description,
    r.recipe           AS recipe,
    r.photo_path       AS photo_path,
    r.total_weight_grams AS total_weight_grams,
    r.is_public        AS is_public,
    r.created_by       AS created_by_id,
    r.entity_status_id AS entity_status_id,
    es.code            AS entity_status_code
FROM recipes r
LEFT JOIN entity_statuses es ON es.id = r.entity_status_id
WHERE r.deleted_at IS NULL;
