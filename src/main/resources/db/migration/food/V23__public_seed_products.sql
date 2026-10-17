-- Seed / imported products often have created_by IS NULL and is_public = 0,
-- so personalized search hides them. Make them visible to all diary users.

UPDATE products
SET is_public = TRUE
WHERE created_by IS NULL
  AND deleted_at IS NULL;

UPDATE products p
JOIN entity_statuses es ON es.code = 'ACTIVE'
SET p.entity_status_id = es.id
WHERE p.created_by IS NULL
  AND p.deleted_at IS NULL;
