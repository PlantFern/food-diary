-- Recipe names, descriptions, instructions and image paths describe content;
-- they are not stable identifiers and must not be globally unique.
-- Keep only the recipe code as the unique business identifier.
ALTER TABLE recipes
    DROP INDEX uc_recipes_description,
    DROP INDEX uc_recipes_name,
    DROP INDEX uc_recipes_photo_path,
    DROP INDEX uc_recipes_recipe,
    MODIFY COLUMN photo_path VARCHAR(255) NULL;
