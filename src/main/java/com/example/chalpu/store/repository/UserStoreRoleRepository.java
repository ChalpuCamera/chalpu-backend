package com.example.chalpu.store.repository;

import com.example.chalpu.store.domain.UserStoreRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface UserStoreRoleRepository extends JpaRepository<UserStoreRole, Long> {
} 