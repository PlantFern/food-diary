-- Fix Illegal mix of collations (utf8mb4_0900_ai_ci vs utf8mb4_unicode_ci)
-- when comparing item_type / itemType in views and queries.

ALTER TABLE food_servings
    MODIFY COLUMN item_type VARCHAR(32)
        CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci
        NOT NULL;

CREATE OR REPLACE VIEW v_food_servings AS
SELECT
    fs.id              AS servingId,
    fs.item_type       AS foodServingType,
    fs.item_id         AS foodServingItemId,
    CASE
        WHEN fs.item_type = CONVERT('PRODUCT' USING utf8mb4) COLLATE utf8mb4_unicode_ci THEN p.code
        WHEN fs.item_type = CONVERT('RECIPE' USING utf8mb4) COLLATE utf8mb4_unicode_ci THEN r.name
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
