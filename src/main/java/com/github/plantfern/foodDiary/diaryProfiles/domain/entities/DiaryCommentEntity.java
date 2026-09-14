package com.github.plantfern.foodDiary.diaryProfiles.domain.entities;


import com.github.plantfern.foodDiary.diaryProfiles.api.CommentableType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Setter
@Getter

@Entity
@Table(
        name = "diary_comments"
)
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
    @Column(name = "commentable_type", nullable = false)
    private CommentableType commentableType;

    @Column(name = "commentable_id")
    private Long commentableId;

    @Column(name = "comment_date")
    private LocalDate commentDate;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "created_by", nullable = false)
    private Long createdById;

    @Column(
            name = "createdAt",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;


    protected DiaryCommentEntity() {}

    public DiaryCommentEntity
            (
                    DiaryProfileEntity diaryProfile,
                    CommentableType commentableType,
                    Long commentableId,
                    String body,
                    LocalDate commentDate,
                    Long createdById
            ) {
        this.diaryProfile = diaryProfile;
        this.commentableType = commentableType;
        this.commentableId = commentableId;
        this.body = body;
        this.commentDate = commentDate;
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
