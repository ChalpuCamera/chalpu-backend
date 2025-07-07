package com.example.chalpu.tag.repository;

import com.example.chalpu.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
    Optional<Tag> findByNameAndIsActiveTrue(String name);
} 