package com.github.plantfern.foodDiary.specialists.domain.entities;


import com.github.plantfern.foodDiary.specialists.api.RelationType;
import com.github.plantfern.foodDiary.specialists.domain.UserRelationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter

@Entity
@Table(
        name="user_relations",
        uniqueConstraints = {
                // 1. Техническая уникальность: пара (user + specialist) не может дублироваться
                @UniqueConstraint(name = "unique_user_relations_diary_profile_id_specialist_id",
                        columnNames = {"diary_profile_id", "specialist_id"})
        })
public class UserRelationEntity {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="diary_profile_id", nullable=false)
    private Long diaryProfileId;

    @Column(name="specialist_id", nullable=false)
    private Long specialistId;

    @Enumerated(EnumType.STRING)
    @Column(name = "relation_type", nullable = false, length = 20)
    private RelationType relationType;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn (name="relation_status_id")
    private UserRelationStatusEntity userRelationStatusEntity;


    public UserRelationStatus getStatusCode(){
        return  UserRelationStatus.valueOf(
                this.userRelationStatusEntity.getCode()
        );
    }
}
