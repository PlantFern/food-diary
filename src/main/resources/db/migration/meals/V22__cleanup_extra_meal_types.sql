-- Diary UI only uses BREAKFAST, LUNCH, SNACK, DINNER, OTHERS.
-- Extra types (MORNING_SNACK, LATE_SNACK, OTHER, …) caused duplicate empty cards.
-- Remap meals that still reference non-canonical types, then delete unused types.

UPDATE meals m
JOIN meal_types mt ON mt.id = m.meal_type_id
JOIN meal_types snack ON snack.code = 'SNACK'
SET m.meal_type_id = snack.id
WHERE UPPER(mt.code) IN ('MORNING_SNACK', 'AFTERNOON_SNACK', 'LATE_SNACK', 'BRUNCH');

UPDATE meals m
JOIN meal_types mt ON mt.id = m.meal_type_id
JOIN meal_types others ON others.code = 'OTHERS'
SET m.meal_type_id = others.id
WHERE UPPER(mt.code) IN ('OTHER', 'OTHERS') AND mt.code <> 'OTHERS';

DELETE mt FROM meal_types mt
WHERE UPPER(mt.code) NOT IN ('BREAKFAST', 'LUNCH', 'SNACK', 'DINNER', 'OTHERS')
  AND NOT EXISTS (SELECT 1 FROM meals m WHERE m.meal_type_id = mt.id);
