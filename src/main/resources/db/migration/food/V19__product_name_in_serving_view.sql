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
