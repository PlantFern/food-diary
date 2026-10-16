-- Preserve full recipe descriptions from development and user-created recipes.
ALTER TABLE recipes
    MODIFY COLUMN description VARCHAR(500) NOT NULL;
