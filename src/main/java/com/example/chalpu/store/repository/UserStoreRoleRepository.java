package com.example.chalpu.store.repository;

import com.example.chalpu.store.domain.UserStoreRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStoreRoleRepository extends JpaRepository<UserStoreRole, Long> {

    @EntityGraph(value = "UserStoreRole.withUserAndStore")
    Page<UserStoreRole> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(value = "UserStoreRole.withUserAndStore")
    List<UserStoreRole> findByUserId(Long userId);

    // 특정 유저의 특정 매장에서의 역할 조회
    @EntityGraph(value = "UserStoreRole.withUserAndStore")
    Optional<UserStoreRole> findByUserIdAndStoreId(Long userId, Long storeId);

    // 특정 매장의 모든 멤버 조회 (활성/비활성 포함)
    @EntityGraph(value = "UserStoreRole.withUserAndStore")
    List<UserStoreRole> findByStoreId(Long storeId);

    // 특정 매장의 활성화된 모든 멤버 조회
    @EntityGraph(value = "UserStoreRole.withUserAndStore")
    List<UserStoreRole> findByStoreIdAndIsActiveTrue(Long storeId);

    // 권한 검증 전용 경량화된 메서드들 (연관 엔티티 조회 없음)
    @Query("SELECT usr FROM UserStoreRole usr WHERE usr.user.id = :userId")
    List<UserStoreRole> findByUserIdWithoutJoin(@Param("userId") Long userId);

    @Query("SELECT usr FROM UserStoreRole usr WHERE usr.user.id = :userId AND usr.store.id = :storeId")
    Optional<UserStoreRole> findByUserIdAndStoreIdWithoutJoin(@Param("userId") Long userId, @Param("storeId") Long storeId);

    // Store만 fetch join하는 메서드 (User 정보 불필요할 때)
    @Query("SELECT usr FROM UserStoreRole usr JOIN FETCH usr.store WHERE usr.user.id = :userId")
    Page<UserStoreRole> findByUserIdWithStoreOnly(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT usr FROM UserStoreRole usr JOIN FETCH usr.store WHERE usr.user.id = :userId")
    List<UserStoreRole> findByUserIdWithStoreOnly(@Param("userId") Long userId);
} 