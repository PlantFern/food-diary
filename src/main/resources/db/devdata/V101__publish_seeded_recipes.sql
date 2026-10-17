-- Publish the four bundled development/demo recipes and mark them approved.
-- Scope this to known seed codes so user-created private recipes remain private.
UPDATE recipes AS r
JOIN entity_statuses AS active_status
  ON active_status.code = 'ACTIVE'
SET r.is_public = TRUE,
    r.entity_status_id = active_status.id
WHERE r.code IN (
    'RECIPE_00000001',
    'RECIPE_00000002',
    'RECIPE_00000003',
    'RECIPE_00000004'
)
  AND r.deleted_at IS NULL;
