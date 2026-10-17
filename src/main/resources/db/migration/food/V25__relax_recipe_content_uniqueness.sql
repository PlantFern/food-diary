-- Recipe names, descriptions and image paths describe content;
-- they are not stable identifiers and must not be globally unique.
-- Keep only the recipe code as the unique business identifier.
-- V16 already removed the unique constraint on recipe instructions.
ALTER TABLE recipes
    DROP INDEX uc_recipes_description,
    DROP INDEX uc_recipes_name,
    DROP INDEX uc_recipes_photo_path,
    MODIFY COLUMN photo_path VARCHAR(255) NULL;
