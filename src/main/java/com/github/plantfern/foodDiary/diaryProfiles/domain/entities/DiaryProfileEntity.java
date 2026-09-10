package com.github.plantfern.foodDiary.diaryProfiles.domain.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Setter
@Getter

@Entity
@Table(name = "diary_profiles")
public class DiaryProfileEntity {

    @Setter(AccessLevel.NONE)
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


    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "diaryProfileEntity"
    )
    private List<ProfileFeatureSettingsEntity> profileFeatureSettingsEntityList;


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
}