CREATE TABLE uploaded_files
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    filename      VARCHAR(255)          NOT NULL,
    original_name VARCHAR(255)          NOT NULL,
    content_type  VARCHAR(100)          NOT NULL,
    size          BIGINT                NOT NULL,
    uploaded_at   datetime              NOT NULL,
    CONSTRAINT pk_uploaded_files PRIMARY KEY (id)
);