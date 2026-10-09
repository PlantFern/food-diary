package com.github.plantfern.foodDiary.diaryProfiles.domain.entities;


import com.github.plantfern.foodDiary.diaryProfiles.api.CommentableType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Setter
@Getter

@Entity
@Table(
        name = "diary_comments"
)
@SQLRestriction("deleted_at is null")
public class DiaryCommentEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "diary_profile_id",
            insertable = false,
            updatable = false
    )
    private DiaryProfileEntity diaryProfile;

    @Column(
            name = "diary_profile_id",
            nullable = false
    )
    private Long diaryProfileId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "commentable_type",
            nullable = false
    )
    private CommentableType commentableType;

    @Column(
            name = "commentable_id",
            nullable = false
    )
    private Long commentableId;

    @NotBlank
    @Size(max = 2000)
    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "created_by", nullable = false)
    private Long createdById;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    protected DiaryCommentEntity() {}

    public DiaryCommentEntity
            (
                    Long diaryProfileId,
                    CommentableType commentableType,
                    Long commentableId,
                    String body,
                    Long createdById
            ) {
        this.diaryProfileId = diaryProfileId;
        this.commentableType = commentableType;
        this.commentableId = commentableId;
        this.body = body;
        this.createdById = createdById;
    }


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void setDeletedDate() {
        if (deletedAt == null) {
            deletedAt = LocalDateTime.now();
        }
    }
}
