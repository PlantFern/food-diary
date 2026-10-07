package com.github.plantfern.foodDiary.users.domain.repositories;

import com.github.plantfern.foodDiary.users.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmailIgnoreCase(String email);
    Optional<UserEntity> findByLoginIgnoreCase(String login);

    boolean existsByEmailIgnoreCase(String email);
    boolean existsByLoginIgnoreCase(String login);

    List<UserEntity> findAllByDeletedAtIsNull();
}
