package com.github.plantfern.foodDiary.common.storage;

import com.github.plantfern.foodDiary.common.storage.domain.UploadedFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UploadFileRepository extends JpaRepository<UploadedFileEntity, Long> {

    Optional<UploadedFileEntity> findByFilename(String filename);
}
