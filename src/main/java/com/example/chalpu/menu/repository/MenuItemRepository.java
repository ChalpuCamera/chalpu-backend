package com.example.chalpu.menu.repository;

import com.example.chalpu.menu.domain.Menu;
import com.example.chalpu.menu.domain.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByMenuAndIsActiveTrue(Menu menu);

    @EntityGraph(value = "MenuItem.withMenuAndFoodItem")
    Optional<MenuItem> findByIdAndIsActiveTrue(Long menuItemId);

    @EntityGraph(value = "MenuItem.withMenuAndFoodItem")
    Page<MenuItem> findByMenuIdAndIsActiveTrue(Long menuId, Pageable pageable);

    List<MenuItem> findByFoodItemIdAndIsActiveTrue(Long foodItemId);
} 