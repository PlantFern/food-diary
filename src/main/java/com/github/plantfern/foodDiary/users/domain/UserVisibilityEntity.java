package com.github.plantfern.foodDiary.users.domain;


import com.github.plantfern.foodDiary.users.domain.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(
        name="user_visibility",
        uniqueConstraints = {
                @UniqueConstraint(
                        name="unique_actor_user_id_target_user_id",
                        columnNames={"actor_user_id", "target_user_id"}
                )
        }
)
public class UserVisibilityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name="actor_user_id",
            nullable = false
    )
    private Long actorUserId;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "actor_user_id",
            insertable = false,
            updatable = false
    )
    private UserEntity actorUser;

    @Column(
            name="target_user_id",
            nullable = false
    )
    private Long targetUserId;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "target_user_id",
            insertable = false,
            updatable = false
    )
    private UserEntity targetUser;

    @Column(
            name="is_extended",
            nullable=false
    )
    private Boolean isExtended;


    protected UserVisibilityEntity() {}

    public UserVisibilityEntity(
            Long actorUserId,
            Long targetUserId,
            Boolean isExtended
    ) {
        this.actorUserId = actorUserId;
        this.targetUserId = targetUserId;
        this.isExtended = isExtended;
    }
}
