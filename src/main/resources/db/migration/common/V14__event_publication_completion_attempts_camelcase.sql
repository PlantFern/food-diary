-- Hibernate PhysicalNamingStrategyStandardImpl expects camelCase column names.
-- V13 renamed most EVENT_PUBLICATION columns but missed these two.

ALTER TABLE event_publication
    CHANGE COLUMN completion_attempts completionAttempts INT NOT NULL,
    CHANGE COLUMN last_resubmission_date lastResubmissionDate DATETIME NULL;

ALTER TABLE event_publication_archive
    CHANGE COLUMN completion_attempts completionAttempts INT NOT NULL,
    CHANGE COLUMN last_resubmission_date lastResubmissionDate DATETIME NULL;
