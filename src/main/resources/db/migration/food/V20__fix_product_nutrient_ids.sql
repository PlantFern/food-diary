-- V12 provides canonical nutrient IDs 1..4 (ENERGY, PROTEIN, FAT, CARBS).
-- V15 uses IDs 5..8 for the corresponding catalog nutrient rows to avoid ID collisions,
-- and its product_nutrients rows point to those IDs. The product list uses the canonical
-- nutrients, so move the four macro associations to IDs 1..4.
--
-- If a product already has the canonical association, keep that one and remove the
-- duplicate remap source first. A self-join is used instead of a target-table subquery
-- because MySQL rejects updating a table while selecting from it in a subquery (1093).

DELETE wrong
FROM product_nutrients AS wrong
JOIN product_nutrients AS canonical
  ON canonical.product_id = wrong.product_id
 AND canonical.nutrient_id = wrong.nutrient_id - 4
WHERE wrong.nutrient_id BETWEEN 5 AND 8;

UPDATE product_nutrients
SET nutrient_id = nutrient_id - 4
WHERE nutrient_id BETWEEN 5 AND 8;
