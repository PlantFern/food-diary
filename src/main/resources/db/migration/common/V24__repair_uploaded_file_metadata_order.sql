-- Earlier FileService.upload passed the stored path and original name
-- to UploadedFileEntity in reverse order. Recover the intended values for
-- rows where original_name contains a known storage path and filename is
-- still just the client's original filename.
CREATE TEMPORARY TABLE uploaded_file_metadata_repair AS
SELECT
    id,
    original_name AS corrected_filename,
    filename AS corrected_original_name
FROM uploaded_files
WHERE (
       original_name LIKE 'food/products/%'
    OR original_name LIKE 'food/recipes/%'
    OR original_name LIKE 'food/product-requests/%'
    OR original_name LIKE 'meals/meals/%'
)
AND filename NOT LIKE '%/%';

UPDATE uploaded_files AS uploaded
JOIN uploaded_file_metadata_repair AS repair
    ON repair.id = uploaded.id
SET uploaded.filename = repair.corrected_filename,
    uploaded.original_name = repair.corrected_original_name;

DROP TEMPORARY TABLE uploaded_file_metadata_repair;
