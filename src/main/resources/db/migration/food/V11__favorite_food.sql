CREATE TABLE favorite_food
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    item_type        VARCHAR(255)          NOT NULL,
    item_id          BIGINT                NOT NULL,
    diary_profile_id BIGINT                NOT NULL,
    CONSTRAINT pk_favorite_food PRIMARY KEY (id)
);

ALTER TABLE favorite_food
    ADD CONSTRAINT unique_product_id UNIQUE (item_id, item_type, diary_profile_id);

ALTER TABLE favorite_food
    ADD CONSTRAINT FK_FAVORITE_FOOD_ON_DIARY_PROFILE FOREIGN KEY (diary_profile_id) REFERENCES diary_profiles (id);
