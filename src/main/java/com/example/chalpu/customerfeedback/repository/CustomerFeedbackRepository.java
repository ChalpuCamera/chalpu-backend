package com.example.chalpu.customerfeedback.repository;

import com.example.chalpu.customerfeedback.domain.CustomerFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerFeedbackRepository extends JpaRepository<CustomerFeedback, Long> {

    List<CustomerFeedback> findByCustomerIdAndIsActiveTrueOrderByCreatedAtDesc(Long customerId);
    
    Page<CustomerFeedback> findByCustomerIdAndIsActiveTrueOrderByCreatedAtDesc(Long customerId, Pageable pageable);
    
    List<CustomerFeedback> findByStoreIdAndIsActiveTrueOrderByCreatedAtDesc(Long storeId);
    
    Page<CustomerFeedback> findByStoreIdAndIsActiveTrueOrderByCreatedAtDesc(Long storeId, Pageable pageable);
    
    List<CustomerFeedback> findByFoodItemIdAndIsActiveTrueOrderByCreatedAtDesc(Long foodItemId);
    
    @Query("SELECT cf FROM CustomerFeedback cf JOIN FETCH cf.customer JOIN FETCH cf.foodItem WHERE cf.store.id = :storeId AND cf.isActive = true ORDER BY cf.createdAt DESC")
    List<CustomerFeedback> findByStoreIdWithCustomerAndFoodItem(@Param("storeId") Long storeId);
    
    int countByCustomerIdAndIsActiveTrue(Long customerId);
    
    int countByStoreIdAndIsActiveTrue(Long storeId);
}