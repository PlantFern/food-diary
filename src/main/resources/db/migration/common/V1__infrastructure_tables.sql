CREATE TABLE event_publication
(
    id                     BINARY(16)   NOT NULL,
    publication_date       datetime     NOT NULL,
    listener_id            VARCHAR      NOT NULL,
    serialized_event       VARCHAR      NOT NULL,
    event_type             VARCHAR      NOT NULL,
    completion_date        datetime     NULL,
    last_resubmission_date datetime     NULL,
    completion_attempts    INT          NOT NULL,
    status                 VARCHAR(255) NULL,
    CONSTRAINT pk_event_publication PRIMARY KEY (id)
);

CREATE TABLE event_publication_archive
(
    id                     BINARY(16)   NOT NULL,
    publication_date       datetime     NOT NULL,
    listener_id            VARCHAR      NOT NULL,
    serialized_event       VARCHAR      NOT NULL,
    event_type             VARCHAR      NOT NULL,
    completion_date        datetime     NULL,
    last_resubmission_date datetime     NULL,
    completion_attempts    INT          NOT NULL,
    status                 VARCHAR(255) NULL,
    CONSTRAINT pk_event_publication_archive PRIMARY KEY (id)
);

CREATE TABLE revchanges
(
    rev        BIGINT       NOT NULL,
    entityname VARCHAR(255) NULL
);

CREATE TABLE revinfo
(
    rev      BIGINT NOT NULL,
    revtstmp BIGINT NULL,
    CONSTRAINT pk_revinfo PRIMARY KEY (rev)
);

ALTER TABLE revchanges
    ADD CONSTRAINT fk_revchanges_on_default_tracking_modified_entities_changelog FOREIGN KEY (rev) REFERENCES revinfo (rev);