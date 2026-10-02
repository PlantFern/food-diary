package com.github.plantfern.foodDiary.common.storage;

import com.github.plantfern.foodDiary.common.storage.domain.UploadedFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UploadFileRepository extends JpaRepository<UploadedFileEntity, Long> {

    Optional<UploadedFileEntity> findByFilename(String filename);
}
