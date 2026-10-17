-- Recipe nutrient rows are stored as nutrient amounts per 100 g, just like product nutrient rows.
-- Include the selected serving's amount as well as its gram weight for every recipe component.
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
    SUM(pn.amount * rc.amount * fs.gram_weight * COALESCE(fs.amount, 1))
        / NULLIF(MAX(recipe_weight.total_grams), 0) AS amountPer100g,
    nu.code AS unitCode
FROM recipe_components rc
JOIN food_servings fs
    ON fs.id = rc.product_serving_id
   AND fs.item_type = CONVERT('PRODUCT' USING utf8mb4) COLLATE utf8mb4_unicode_ci
JOIN (
    SELECT
        rc2.recipe_id,
        SUM(rc2.amount * fs2.gram_weight * COALESCE(fs2.amount, 1)) AS total_grams
    FROM recipe_components rc2
    JOIN food_servings fs2
        ON fs2.id = rc2.product_serving_id
       AND fs2.item_type = CONVERT('PRODUCT' USING utf8mb4) COLLATE utf8mb4_unicode_ci
    GROUP BY rc2.recipe_id
) recipe_weight ON recipe_weight.recipe_id = rc.recipe_id
JOIN product_nutrients pn ON pn.product_id = fs.item_id
JOIN nutrients n ON n.id = pn.nutrient_id
JOIN nutrient_units nu ON nu.id = n.unit_id
GROUP BY rc.recipe_id, pn.nutrient_id, n.code, nu.code;

CREATE OR REPLACE VIEW v_recipe_nutrients AS
SELECT
    rc.recipe_id   AS recipeId,
    pn.nutrient_id AS nutrientId,
    r.name         AS recipeCode,
    n.code         AS nutrientCode,
    SUM(pn.amount * rc.amount * fs.gram_weight * COALESCE(fs.amount, 1))
        / NULLIF(MAX(recipe_weight.total_grams), 0) AS amountPer100g,
    nu.code        AS unitCode
FROM recipe_components rc
JOIN recipes r ON r.id = rc.recipe_id AND r.deleted_at IS NULL
JOIN food_servings fs
    ON fs.id = rc.product_serving_id
   AND fs.item_type = CONVERT('PRODUCT' USING utf8mb4) COLLATE utf8mb4_unicode_ci
JOIN (
    SELECT
        rc2.recipe_id,
        SUM(rc2.amount * fs2.gram_weight * COALESCE(fs2.amount, 1)) AS total_grams
    FROM recipe_components rc2
    JOIN food_servings fs2
        ON fs2.id = rc2.product_serving_id
       AND fs2.item_type = CONVERT('PRODUCT' USING utf8mb4) COLLATE utf8mb4_unicode_ci
    GROUP BY rc2.recipe_id
) recipe_weight ON recipe_weight.recipe_id = rc.recipe_id
JOIN product_nutrients pn ON pn.product_id = fs.item_id
JOIN nutrients n ON n.id = pn.nutrient_id
JOIN nutrient_units nu ON nu.id = n.unit_id
GROUP BY rc.recipe_id, pn.nutrient_id, r.name, n.code, nu.code;
