package com.example.chalpu.menu.repository;

import com.example.chalpu.menu.domain.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
} 