package com.github.plantfern.foodDiary.specialists.domain.repositories;


import com.github.plantfern.foodDiary.specialists.domain.entities.SpecialistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SpecialistRepository extends JpaRepository<SpecialistEntity, Long> {
    Optional<SpecialistEntity> findByUserId(Long userId);

    Optional<SpecialistEntity> findTopByUserIdAndDeletedAtIsNotNullOrderByDeletedAtDesc(Long userId);

    boolean existsByUserId(Long userId);
    boolean existsByUserIdAndDeletedAtIsNull(Long userId);

    @Query(value = """
            SELECT s.id AS id,
                   s.user_id AS userId,
                   s.is_active AS isActive,
                   u.email AS email,
                   u.login AS login
            FROM specialists s
            INNER JOIN users u ON u.id = s.user_id
            WHERE s.deleted_at IS NULL
              AND (s.is_active IS NULL OR s.is_active = TRUE)
              AND (
                    :q IS NULL OR TRIM(:q) = '' OR
                    LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%')) OR
                    LOWER(COALESCE(u.login, '')) LIKE LOWER(CONCAT('%', :q, '%'))
              )
            ORDER BY u.email
            LIMIT 50
            """, nativeQuery = true)
    List<SpecialistSearchRow> searchByEmailOrLogin(@Param("q") String q);

    interface SpecialistSearchRow {
        Long getId();
        Long getUserId();
        Boolean getIsActive();
        String getEmail();
        String getLogin();
    }
}
