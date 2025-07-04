package com.example.chalpu.store.repository;

import com.example.chalpu.store.domain.UserStoreRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
 
@Repository
public interface UserStoreRoleRepository extends JpaRepository<UserStoreRole, Long> {
    Page<UserStoreRole> findByUserId(Long userId, Pageable pageable);
    List<UserStoreRole> findByUserId(Long userId);
    
    // 특정 유저의 특정 매장에서의 역할 조회
    Optional<UserStoreRole> findByUserIdAndStoreId(Long userId, Long storeId);
    
    // 특정 매장의 모든 멤버 조회
    List<UserStoreRole> findByStoreId(Long storeId);
} 