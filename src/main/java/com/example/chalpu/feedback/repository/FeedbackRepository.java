package com.example.chalpu.feedback.repository;

import com.example.chalpu.feedback.domain.Feedback;
import com.example.chalpu.feedback.domain.FeedbackType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    @Query("SELECT f FROM Feedback f WHERE f.userId = :userId ORDER BY f.createdAt DESC")
    List<Feedback> findByUserIdAndFeedbackType(@Param("userId") Long userId);
}