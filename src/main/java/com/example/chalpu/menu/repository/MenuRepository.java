package com.example.chalpu.menu.repository;

import com.example.chalpu.menu.domain.Menu;
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
public interface MenuRepository extends JpaRepository<Menu, Long> {

    @EntityGraph(value = "Menu.withStore")
    Page<Menu> findByStoreIdAndIsActiveTrue(Long storeId, Pageable pageable);

    @Query("SELECT m FROM Menu m WHERE m.store.id = :storeId AND m.isActive = true")
    Page<Menu> findByStoreIdAndIsActiveTrueWithoutJoin(@Param("storeId") Long storeId, Pageable pageable);

    @EntityGraph("Menu.withStore")
    @Query("SELECT m FROM Menu m WHERE m.store.id = :storeId AND m.isActive = true")
    List<Menu> findAllByStoreIdAndIsActiveTrueWithStore(@Param("storeId") Long storeId);

    @EntityGraph("Menu.withStore")
    @Query("SELECT m FROM Menu m WHERE m.id = :menuId AND m.isActive = true")
    Optional<Menu> findByIdAndIsActiveTrueWithStore(@Param("menuId") Long menuId);

    Optional<Menu> findByIdAndIsActiveTrue(Long menuId);

    // 경량화된 조회 메서드 (연관 엔티티 조회 없음)
    @Query("SELECT m FROM Menu m WHERE m.id = :menuId AND m.isActive = true")
    Optional<Menu> findByIdAndIsActiveTrueWithoutJoin(@Param("menuId") Long menuId);
} 