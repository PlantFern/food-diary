package com.github.plantfern.foodDiary.diaryProfiles.domain.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter

@Entity
@Table(
        name = "genders",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "code"
        })
)
public class GenderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String code;

    @OneToMany(
            mappedBy = "gender",
            fetch = FetchType.LAZY
    )
    private Set<DiaryProfileEntity> diaryProfileGenders = new HashSet<>();


    protected GenderEntity() {}
    public GenderEntity(String code){
        this.code = code;
    }
}