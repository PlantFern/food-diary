-- Seed data wrote macros as nutrient_id 5..8 while reference nutrients are 1..4
-- (ENERGY, PROTEIN, FAT, CARBS). View v_product_nutrients INNER JOINs nutrients,
-- so those rows never appeared in API → UI showed "—" for everything.

-- Prefer update when target id is free for that product
UPDATE product_nutrients pn
LEFT JOIN nutrients n ON n.id = pn.nutrient_id
SET pn.nutrient_id = pn.nutrient_id - 4
WHERE pn.nutrient_id BETWEEN 5 AND 8
  AND n.id IS NULL
  AND NOT EXISTS (
      SELECT 1
      FROM product_nutrients pn2
      WHERE pn2.product_id = pn.product_id
        AND pn2.nutrient_id = pn.nutrient_id - 4
  );

-- Drop orphan rows that still point to non-existent nutrients 5..8
-- (conflict case: both wrong and correct id already present)
DELETE pn
FROM product_nutrients pn
LEFT JOIN nutrients n ON n.id = pn.nutrient_id
WHERE pn.nutrient_id BETWEEN 5 AND 8
  AND n.id IS NULL;
