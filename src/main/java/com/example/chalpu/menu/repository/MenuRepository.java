package com.example.chalpu.menu.repository;

import com.example.chalpu.menu.domain.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    
    Page<Menu> findByStoreIdAndIsActiveTrue(Long storeId, Pageable pageable);

    Optional<Menu> findByIdAndIsActiveTrue(Long menuId);
} 