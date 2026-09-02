package com.github.plantfern.foodDiary.diaryProfiles.domain.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.config.ConfigDataEnvironmentUpdateListener;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Setter
@Getter

@Entity
@Table(name = "diary_profiles")
public class DiaryProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name="user_id",
            nullable=false
    )
    private Long userId;

    @Column
    @Min(50) @Max(250)
    private Float height;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "gender_id")
    private GenderEntity gender;


    protected DiaryProfileEntity(){ }

    public DiaryProfileEntity(
            Long userId,
            Float height,
            LocalDate birthDate,
            GenderEntity gender
            ){
        this.userId = userId;
        this.height = height;
        this.birthDate = birthDate;
        this.gender = gender;
    }


    public void changeGender(GenderEntity gender) {
        this.gender = gender;
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


    public boolean belongsTo(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    public void ensureOwnedBy(Long userId){
        if(!belongsTo(userId)) {
            throw new AccessDeniedException("Access denied to diary: " + this.id);
        }
    }
}