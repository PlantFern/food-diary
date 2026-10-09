package com.github.plantfern.foodDiary.common.storage.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;


@NoArgsConstructor(access = AccessLevel.PROTECTED)

@Setter
@Getter

@Entity
@Table(name = "uploaded_files")
public class UploadedFileEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String filename;

    @Column(nullable = false)
    private String originalName;

    @Column(
            nullable = false,
            length = 100
    )
    private String contentType;

    @Min(1)
    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;


    public UploadedFileEntity(String originalName, String filename, String contentType, Long size, LocalDateTime uploadedAt) {
        this.originalName = originalName;
        this.filename = filename;
        this.contentType = contentType;
        this.size = size;
        this.uploadedAt = uploadedAt;
    }

    @PrePersist
    protected void setUpdatedAt() {
        this.uploadedAt = LocalDateTime.now();
    }
}
