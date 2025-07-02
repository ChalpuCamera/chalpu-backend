package com.example.chalpu.store.repository;

import com.example.chalpu.store.domain.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("SELECT s FROM Store s JOIN UserStoreRole usr ON s.id = usr.store.id WHERE usr.user.id = :userId")
    List<Store> findStoresByUserId(@Param("userId") Long userId);
    
    @Query("SELECT s FROM Store s JOIN UserStoreRole usr ON s.id = usr.store.id WHERE usr.user.id = :userId")
    Page<Store> findStoresByUserId(@Param("userId") Long userId, Pageable pageable);
} 