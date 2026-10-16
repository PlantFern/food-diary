-- Reference data required for onboarding and diary UI

INSERT INTO genders (code) VALUES ('MALE');
INSERT INTO genders (code) VALUES ('FEMALE');

INSERT INTO meal_types (code) VALUES ('BREAKFAST');
INSERT INTO meal_types (code) VALUES ('LUNCH');
INSERT INTO meal_types (code) VALUES ('SNACK');
INSERT INTO meal_types (code) VALUES ('DINNER');
INSERT INTO meal_types (code) VALUES ('OTHERS');

INSERT INTO nutrient_units (code) VALUES ('kcal');
INSERT INTO nutrient_units (code) VALUES ('g');

INSERT INTO nutrients (code, unit_id)
SELECT 'ENERGY', id FROM nutrient_units WHERE code = 'kcal' LIMIT 1;
INSERT INTO nutrients (code, unit_id)
SELECT 'PROTEIN', id FROM nutrient_units WHERE code = 'g' LIMIT 1;
INSERT INTO nutrients (code, unit_id)
SELECT 'FAT', id FROM nutrient_units WHERE code = 'g' LIMIT 1;
INSERT INTO nutrients (code, unit_id)
SELECT 'CARBS', id FROM nutrient_units WHERE code = 'g' LIMIT 1;

INSERT INTO serving_units (code) VALUES ('g');
INSERT INTO serving_units (code) VALUES ('ml');
INSERT INTO serving_units (code) VALUES ('pcs');

INSERT INTO entity_statuses (code) VALUES ('ACTIVE');
INSERT INTO entity_statuses (code) VALUES ('DRAFT');
INSERT INTO entity_statuses (code) VALUES ('ARCHIVED');

INSERT INTO data_sources (code) VALUES ('USER');
INSERT INTO data_sources (code) VALUES ('SYSTEM');

INSERT INTO user_relation_statuses (code) VALUES ('PENDING');
INSERT INTO user_relation_statuses (code) VALUES ('ACTIVE');
INSERT INTO user_relation_statuses (code) VALUES ('CANCELED');
INSERT INTO user_relation_statuses (code) VALUES ('ENDED');

INSERT INTO roles (code) VALUES ('USER');
INSERT INTO roles (code) VALUES ('DIARY_PROFILE');
INSERT INTO roles (code) VALUES ('SPECIALIST');
INSERT INTO roles (code) VALUES ('ADMIN');
