package com.github.plantfern.foodDiary.users.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name="user_visibility")
public class UserVisibilityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name="actor_user_id",
            nullable = false
    )
    private Long actorUserId;

    @Column(
            name="target_user_id",
            nullable = false
    )
    private Long targetUserId;
}
