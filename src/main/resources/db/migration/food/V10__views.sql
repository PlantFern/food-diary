CREATE OR REPLACE VIEW v_meal_day_records AS
SELECT
    fr.id              AS recordId,
    fr.meal_data_id    AS mealId,
    fr.serving_id      AS servingId,
    fr.amount          AS amount,
    fr.eaten_at        AS eatenAt,
    m.diary_profile_id AS diaryProfileId,
    m.meal_type_id     AS mealTypeId,
    m.date             AS mealDate,
    m.generated_from_template_id AS generatedFromTemplateId,
    m.photo_path       AS photoPath,
    mt.code            AS mealTypeCode
FROM food_records fr
JOIN meals m ON m.id = fr.meal_data_id AND m.deleted_at IS NULL
JOIN meal_types mt ON mt.id = m.meal_type_id;

CREATE OR REPLACE VIEW v_food_servings AS
SELECT
    fs.id              AS servingId,
    fs.item_type       AS foodServingType,
    fs.item_id         AS foodServingItemId,
    CASE
        WHEN fs.item_type = CONVERT('PRODUCT' USING utf8mb4) COLLATE utf8mb4_unicode_ci
            THEN COALESCE(NULLIF(TRIM(p.description), ''), p.code)
        WHEN fs.item_type = CONVERT('RECIPE' USING utf8mb4) COLLATE utf8mb4_unicode_ci
            THEN r.name
        ELSE NULL
    END                AS itemName,
    CASE
        WHEN fs.item_type = CONVERT('PRODUCT' USING utf8mb4) COLLATE utf8mb4_unicode_ci THEN p.photo_path
        WHEN fs.item_type = CONVERT('RECIPE' USING utf8mb4) COLLATE utf8mb4_unicode_ci THEN r.photo_path
        ELSE NULL
    END                AS itemPhotoPath,
    fs.amount          AS servingAmount,
    fs.gram_weight     AS gramWeight,
    fs.serving_unit_id AS servingUnitId,
    su.code            AS servingUnitCode
FROM food_servings fs
LEFT JOIN products p
    ON fs.item_type = CONVERT('PRODUCT' USING utf8mb4) COLLATE utf8mb4_unicode_ci
   AND p.id = fs.item_id
   AND p.deleted_at IS NULL
LEFT JOIN recipes r
    ON fs.item_type = CONVERT('RECIPE' USING utf8mb4) COLLATE utf8mb4_unicode_ci
   AND r.id = fs.item_id
   AND r.deleted_at IS NULL
LEFT JOIN serving_units su ON su.id = fs.serving_unit_id;

CREATE OR REPLACE VIEW v_product_nutrients AS
SELECT
    pn.product_id  AS productId,
    pn.nutrient_id AS nutrientId,
    p.code         AS productCode,
    n.code         AS nutrientCode,
    pn.amount      AS amountPer100g,
    nu.code        AS unitCode
FROM product_nutrients pn
JOIN products p ON p.id = pn.product_id AND p.deleted_at IS NULL
JOIN nutrients n ON n.id = pn.nutrient_id
JOIN nutrient_units nu ON nu.id = n.unit_id;

CREATE OR REPLACE VIEW v_item_nutrients AS
SELECT
    pn.product_id AS itemId,
    CONVERT('PRODUCT' USING utf8mb4) COLLATE utf8mb4_unicode_ci AS itemType,
    pn.nutrient_id AS nutrientId,
    n.code AS nutrientCode,
    pn.amount AS amountPer100g,
    nu.code AS unitCode
FROM product_nutrients pn
JOIN nutrients n ON n.id = pn.nutrient_id
JOIN nutrient_units nu ON nu.id = n.unit_id
UNION ALL
SELECT
    rc.recipe_id AS itemId,
    CONVERT('RECIPE' USING utf8mb4) COLLATE utf8mb4_unicode_ci AS itemType,
    pn.nutrient_id AS nutrientId,
    n.code AS nutrientCode,
    SUM(pn.amount * (rc.amount * fs.gram_weight) / 100.0) AS amountPer100g,
    nu.code AS unitCode
FROM recipe_components rc
JOIN food_servings fs ON fs.id = rc.product_serving_id
JOIN product_nutrients pn
    ON pn.product_id = fs.item_id
   AND fs.item_type = CONVERT('PRODUCT' USING utf8mb4) COLLATE utf8mb4_unicode_ci
JOIN nutrients n ON n.id = pn.nutrient_id
JOIN nutrient_units nu ON nu.id = n.unit_id
GROUP BY rc.recipe_id, pn.nutrient_id, n.code, nu.code;

CREATE OR REPLACE VIEW v_products_basic AS
SELECT
    p.id               AS productId,
    p.code             AS productCode,
    p.description      AS productDescription,
    p.photo_path       AS photoPath,
    p.is_public        AS isPublic,
    p.created_by       AS createdById,
    p.category_id      AS categoryId,
    c.code             AS categoryCode,
    p.entity_status_id AS entityStatusId,
    es.code            AS entityStatusCode,
    pds.data_source_id AS dataSourceId,
    ds.code            AS dataSourceCode,
    bp.base_serving_id AS baseServingId,
    fs.amount          AS servingAmount,
    fs.gram_weight     AS gramWeightPerServing,
    su.code            AS servingUnitCode
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
    rc.recipe_id   AS recipeId,
    pn.nutrient_id AS nutrientId,
    r.name         AS recipeCode,
    n.code         AS nutrientCode,
    SUM(pn.amount * (rc.amount * fs.gram_weight) / 100.0) AS amountPer100g,
    nu.code        AS unitCode
FROM recipe_components rc
JOIN recipes r ON r.id = rc.recipe_id AND r.deleted_at IS NULL
JOIN food_servings fs ON fs.id = rc.product_serving_id
JOIN product_nutrients pn
    ON pn.product_id = fs.item_id
   AND fs.item_type = CONVERT('PRODUCT' USING utf8mb4) COLLATE utf8mb4_unicode_ci
JOIN nutrients n ON n.id = pn.nutrient_id
JOIN nutrient_units nu ON nu.id = n.unit_id
GROUP BY rc.recipe_id, pn.nutrient_id, r.name, n.code, nu.code;

CREATE OR REPLACE VIEW v_recipes_basic AS
SELECT
    r.id               AS recipeId,
    r.name             AS recipeName,
    r.description      AS recipeDescription,
    r.recipe           AS recipe,
    r.photo_path       AS photoPath,
    r.total_weight_grams AS totalWeightGrams,
    r.is_public        AS isPublic,
    r.created_by       AS createdById,
    r.entity_status_id AS entityStatusId,
    es.code            AS entityStatusCode
FROM recipes r
LEFT JOIN entity_statuses es ON es.id = r.entity_status_id
WHERE r.deleted_at IS NULL;
