package com.example.chalpu.menu.repository;

import com.example.chalpu.menu.domain.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    
    List<Menu> findByStoreId(Long storeId);
    
    Page<Menu> findByStoreId(Long storeId, Pageable pageable);
} 