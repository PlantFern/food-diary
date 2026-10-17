CREATE TABLE event_publication
(
    id                     BINARY(16)   NOT NULL,
    publicationDate       datetime     NOT NULL,
    listenerId            VARCHAR(512) NOT NULL,
    serializedEvent       TEXT         NOT NULL,
    eventType             VARCHAR(512) NOT NULL,
    completionDate        datetime     NULL,
    lastResubmissionDate datetime     NULL,
    completionAttempts    INT          NOT NULL,
    status                 VARCHAR(255) NULL,
    CONSTRAINT pk_event_publication PRIMARY KEY (id)
);

CREATE TABLE event_publication_archive
(
    id                     BINARY(16)   NOT NULL,
    publicationDate       datetime     NOT NULL,
    listenerId            VARCHAR(512) NOT NULL,
    serializedEvent       TEXT         NOT NULL,
    eventType             VARCHAR(512) NOT NULL,
    completionDate        datetime     NULL,
    lastResubmissionDate datetime     NULL,
    completionAttempts    INT          NOT NULL,
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
