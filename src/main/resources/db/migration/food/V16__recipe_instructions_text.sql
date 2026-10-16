-- Recipe instructions can exceed 255 characters and must not be used as a unique key.
ALTER TABLE recipes
    DROP INDEX uc_recipes_recipe,
    MODIFY COLUMN recipe TEXT NOT NULL;
