-- Align ALL EVENT_PUBLICATION columns with PhysicalNamingStrategyStandardImpl (camelCase).
-- Hibernate expects Java field names as column names when using StandardImpl.

-- Main table
ALTER TABLE event_publication
    CHANGE COLUMN publication_date publicationDate DATETIME NOT NULL,
    CHANGE COLUMN listener_id listenerId VARCHAR(512) NOT NULL,
    CHANGE COLUMN serialized_event serializedEvent TEXT NOT NULL,
    CHANGE COLUMN event_type eventType VARCHAR(512) NOT NULL,
    CHANGE COLUMN completion_date completionDate DATETIME NULL;

-- Only rename if still in snake_case (safe if already renamed in V12)
-- completion_attempts / last_resubmission_date may already be camelCase after local V12

-- Archive table
ALTER TABLE event_publication_archive
    CHANGE COLUMN publication_date publicationDate DATETIME NOT NULL,
    CHANGE COLUMN listener_id listenerId VARCHAR(512) NOT NULL,
    CHANGE COLUMN serialized_event serializedEvent TEXT NOT NULL,
    CHANGE COLUMN event_type eventType VARCHAR(512) NOT NULL,
    CHANGE COLUMN completion_date completionDate DATETIME NULL;
